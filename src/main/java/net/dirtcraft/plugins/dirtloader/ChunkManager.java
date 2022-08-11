package net.dirtcraft.plugins.dirtloader;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

public class ChunkManager {
	public static void addChunksToPlayer(Player player, String type, int amount, CommandSender sender) {
		File file = new File(DirtLoader.plugin.getDataFolder() + "/playerdata", player.getUniqueId() + ".yml");
		if (file.exists()) {
			FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
			if (type.equals("online")) {
				playerFile.set("available.online", playerFile.getInt("available.online") + amount);
			} else {
				playerFile.set("available.offline", playerFile.getInt("available.offline") + amount);
			}

			sender.sendMessage(Strings.PREFIX + ChatColor.GRAY + "Successfully " + ChatColor.GREEN + "added " + ChatColor.GRAY + amount + " " + ChatColor.DARK_AQUA + type + ChatColor.GRAY + " chunk" + (amount > 1 ? "s" : "") + " to " + player.getName() + ".");
			Utilities.savePlayerFile(playerFile, file, player);
			if (Utilities.config.getBoolean("general.player-add-message")) {
				player.sendMessage(Strings.PREFIX + ChatColor.GRAY + "You have been given " + ChatColor.GREEN + amount + " " + ChatColor.DARK_AQUA + type + ChatColor.GRAY + " chunk" + (amount > 1 ? "s" : "") + ".");
			}
		}
	}

	public static void removeChunksFromPlayer(Player player, String type, int amount, CommandSender sender) {
		File file = new File(DirtLoader.plugin.getDataFolder() + "/playerdata", player.getUniqueId() + ".yml");
		if (file.exists()) {
			FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
			if (type.equals("online")) {
				if (playerFile.getInt("available.online") == 0) {
					sender.sendMessage(Strings.CANT_REMOVE_MORE_CHUNKS);
					return;
				}
				playerFile.set("available.online", playerFile.getInt("available.online") - amount);
			} else {
				if (playerFile.getInt("available.offline") == 0) {
					sender.sendMessage(Strings.CANT_REMOVE_MORE_CHUNKS);
					return;
				}
				playerFile.set("available.offline", playerFile.getInt("available.offline") - amount);
			}

			sender.sendMessage(Strings.PREFIX + ChatColor.GRAY + "Successfully " + ChatColor.RED + "removed " + ChatColor.GRAY + amount + " " + ChatColor.DARK_AQUA + type + ChatColor.GRAY + " chunk" + (amount > 1 ? "s" : "") + " from " + player.getName() + ".");
			Utilities.savePlayerFile(playerFile, file, player);
			if (Utilities.config.getBoolean("general.player-remove-message")) {
				player.sendMessage(Strings.PREFIX + ChatColor.RED + amount + " " + ChatColor.DARK_AQUA + type + ChatColor.GRAY + " chunk" + (amount > 1 ? "s" : "") + " have been removed.");
			}
		}
	}

	public static void saveChunkToPlayer(Player player, String world, int chunkX, int chunkY, String type) {
		File file = new File(DirtLoader.plugin.getDataFolder() + "/playerdata", player.getUniqueId() + ".yml");
		FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
		List<String> chunks = playerFile.getStringList("chunks");
		String timeStamp = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());

		if (playerFile.getInt("used." + type) >= playerFile.getInt("available." + type)) {
			if (Utilities.config.getBoolean("debug-messages")) {
				DirtLoader.plugin.getLogger().log(Level.INFO, "Player " + player.getName() + " has reached their limit of " + type + " chunks!");
			}
			player.sendMessage(Strings.NOT_ENOUGH_LOADERS);
			playFailureSound(player);
			return;
		}

		for (String chunk : chunks) {
			if (chunk.contains(world + "#" + chunkX + "#" + chunkY)) {
				player.sendMessage(Strings.ALREADY_LOADED);
				playFailureSound(player);
				return;
			}
		}

		chunks.add(world + "#" + chunkX + "#" + chunkY + "#" + type + "#" + timeStamp);
		playerFile.set("chunks", chunks);
		int used = playerFile.getInt("used." + type);
		playerFile.set("used." + type, used + 1);
		Utilities.savePlayerFile(playerFile, file, player);

		Chunk chunk = player.getLocation().getChunk();
		loadChunk(chunk);

		player.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Successfully created chunkloader!");
		playSuccessSound(player);
	}

	public static void unloadChunkOfPlayer(CommandSender sender, Player player, String chunk) {
		File file = new File(DirtLoader.plugin.getDataFolder() + "/playerdata", player.getUniqueId() + ".yml");
		if (file.exists()) {
			FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
			List<String> chunks = playerFile.getStringList("chunks");
			boolean chunkFound = false;
			for (String chunkString : chunks) {
				if (chunkString.equals(chunk)) {
					chunks.remove(chunkString);
					chunkFound = true;
					break;
				}
			}

			if (!chunkFound) {
				return;
			}

			playerFile.set("chunks", chunks);
			int used = playerFile.getInt("used." + chunk.split("#")[3]);
			playerFile.set("used." + chunk.split("#")[3], used - 1);
			Utilities.savePlayerFile(playerFile, file, player);
			if (!sender.hasPermission(Permissions.UNLOAD_OTHER)) {
				sender.sendMessage(Strings.PREFIX + ChatColor.GRAY + "Successfully " + ChatColor.RED + "unloaded " + ChatColor.GRAY + chunk.split("#")[3] + " chunk ( " + ChatColor.DARK_AQUA + chunk.split("#")[1] + ChatColor.GRAY + " | " + ChatColor.DARK_AQUA + chunk.split("#")[2] + ChatColor.GRAY + " ).");
			} else {
				sender.sendMessage(Strings.PREFIX + ChatColor.GRAY + "Successfully " + ChatColor.RED + "unloaded " + ChatColor.GRAY + chunk.split("#")[3] + " chunk ( " + ChatColor.DARK_AQUA + chunk.split("#")[1] + ChatColor.GRAY + " | " + ChatColor.DARK_AQUA + chunk.split("#")[2] + ChatColor.GRAY + " ) from " + player.getName() + ".");
			}

			Chunk chunkToUnload = player.getWorld().getChunkAt(Integer.parseInt(chunk.split("#")[1]), Integer.parseInt(chunk.split("#")[2]));
			unloadChunk(chunkToUnload);
		}
	}

	public static void loadAllChunks(Player player) {
		FileConfiguration playerFile = Utilities.getPlayerFile(player);
		for (String chunk : playerFile.getStringList("chunks")) {
			Chunk chunkToLoad = player.getWorld().getChunkAt(Integer.parseInt(chunk.split("#")[1]), Integer.parseInt(chunk.split("#")[2]));
			loadChunk(chunkToLoad);
		}
		DirtLoader.plugin.getLogger().log(Level.INFO, "Loaded " + playerFile.getStringList("chunks").size() + " chunks of " + player.getName() + ".");
	}

	public static void unloadAllChunks(Player player) {
		FileConfiguration playerFile = Utilities.getPlayerFile(player);
		for (String chunk : playerFile.getStringList("chunks")) {
			Chunk chunkToUnload = player.getWorld().getChunkAt(Integer.parseInt(chunk.split("#")[1]), Integer.parseInt(chunk.split("#")[2]));
			unloadChunk(chunkToUnload);
		}
		DirtLoader.plugin.getLogger().log(Level.INFO, "Unloaded " + playerFile.getStringList("chunks").size() + " chunks of " + player.getName() + ".");
	}

	private static void loadChunk(Chunk chunk) {
		if (Utilities.config.getBoolean("debug-messages")) {
			DirtLoader.plugin.getLogger().log(Level.INFO, "Loading chunk " + chunk.getX() + " | " + chunk.getZ() + ".");
		}
		chunk.load();
		chunk.setForceLoaded(true);
	}

	private static void unloadChunk(Chunk chunk) {
		if (Utilities.config.getBoolean("debug-messages")) {
			DirtLoader.plugin.getLogger().log(Level.INFO, "Unloading chunk " + chunk.getX() + " | " + chunk.getZ() + ".");
		}
		chunk.unload(true);
		chunk.setForceLoaded(false);
	}

	private static void playSuccessSound(Player player) {
		if (Utilities.config.getBoolean("general.play-success-sound")) {
			String sound = Utilities.config.getString("general.success-sound");
			if (sound == null) {
				sound = "minecraft:entity.experience_orb.pickup";
			}
			player.playSound(player.getLocation(), sound, 1, 1);
		}
	}

	private static void playFailureSound(Player player) {
		if (Utilities.config.getBoolean("general.play-failure-sound")) {
			String sound = Utilities.config.getString("general.failure-sound");
			if (sound == null) {
				sound = "minecraft:entity.creeper.death";
			}
			player.playSound(player.getLocation(), sound, 1, 1);
		}
	}
}
