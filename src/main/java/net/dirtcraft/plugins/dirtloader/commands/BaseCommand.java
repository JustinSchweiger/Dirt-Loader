package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.DirtLoader;
import net.dirtcraft.plugins.dirtloader.Permissions;
import net.dirtcraft.plugins.dirtloader.Strings;
import net.dirtcraft.plugins.dirtloader.Utilities;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class BaseCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission(Permissions.LOAD)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return false;
		}

		if (sender instanceof Player) {
			Utilities.generatePlayerFile((Player) sender);
		}

		if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
			ArrayList<String> listings = getListings(sender);
			StringBuilder message = new StringBuilder();
			for (String listing : listings) {
				message.append(listing);
			}
			sender.sendMessage(Strings.BAR_TOP + message + Strings.BAR_BOTTOM);
			return true;
		} else if (args[0].equalsIgnoreCase("load")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Strings.NO_CONSOLE);
				return false;
			}

			if (args.length != 2) {
				sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl load <online/offline>");
				return false;
			}

			if (!(args[1].equalsIgnoreCase("online") || args[1].equalsIgnoreCase("offline"))) {
				sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl load <online/offline>");
				return false;
			}

			String type = args[1];
			Player player = (Player) sender;

			Chunk currentChunk = player.getLocation().getChunk();
			int chunkX = currentChunk.getX();
			int chunkZ = currentChunk.getZ();
			String world = currentChunk.getWorld().getName();
			Utilities.saveChunkToPlayer(player, world, chunkX, chunkZ, type);
			return true;
		} else if (args[0].equalsIgnoreCase("chunks") && sender.hasPermission(Permissions.CHUNKS)) {
			if (args.length != 5) {
				sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl chunks <add/remove> <user> <type> <amount>");
				return false;
			}

			Player player = Bukkit.getPlayer(args[2]);
			boolean isOnlinePlayer = player != null;

			if (!((args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) && (args[3].equalsIgnoreCase("online") || args[3].equalsIgnoreCase("offline")) || !StringUtils.isNumeric(args[4]))) {
				sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl chunks <add/remove> <user> <type> <amount>");
				return false;
			}

			if (!isOnlinePlayer) {
				sender.sendMessage(Strings.PLAYER_NOT_FOUND);
				return false;
			}

			String action = args[1];
			String type = args[3];
			int amount = Integer.parseInt(args[4]);

			if (action.equalsIgnoreCase("add")) {
				Utilities.addChunksToPlayer(player, type, amount, sender);
			} else {
				Utilities.removeChunksFromPlayer(player, type, amount, sender);
			}

			return true;
		} else if (args[0].equalsIgnoreCase("bal") && (sender.hasPermission(Permissions.BAL) || sender.hasPermission(Permissions.BAL_USER))) {
			if (sender instanceof Player) {
				if (sender.hasPermission(Permissions.BAL) && !sender.hasPermission(Permissions.BAL_USER)) {
					showBal(sender, (Player) sender);
					return true;
				} else if (sender.hasPermission(Permissions.BAL_USER)) {
					Player player = null;
					if (args.length == 2) {
						player = Bukkit.getPlayer(args[1]);
					}

					if (player == null) {
						player = (Player) sender;
					}

					showBal(sender, player);
					return true;
				}
			} else {
				if (args.length != 2) {
					sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl bal <user>");
					return false;
				}

				Player player = Bukkit.getPlayer(args[1]);
				if (player == null) {
					sender.sendMessage(Strings.PLAYER_NOT_FOUND);
					return false;
				}
				showBal(sender, player);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("list") && (sender.hasPermission(Permissions.LIST) || sender.hasPermission(Permissions.LIST_USER))) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Strings.NO_CONSOLE);
				return false;
			}

			if (sender.hasPermission(Permissions.LIST) && !sender.hasPermission(Permissions.LIST_USER)) {
				if (args.length == 2) {
					if (StringUtils.isNumeric(args[1])) {
						int page = Integer.parseInt(args[1]);
						showList(sender, (Player) sender, page);
						return true;
					} else {
						sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl list [page]");
						return false;
					}
				} else if (args.length == 1) {
					showList(sender, (Player) sender, 1);
					return true;
				} else {
					sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl list [page]");
					return false;
				}
			} else if (sender.hasPermission(Permissions.LIST_USER)) {
				if (args.length == 1) {
					showList(sender, (Player) sender, 1);
					return true;
				} else if (args.length == 2) {
					Player player = Bukkit.getPlayer(args[1]);
					if (StringUtils.isNumeric(args[1])) {
						int page = Integer.parseInt(args[1]);
						showList(sender, (Player) sender, page);
						return true;
					} else if (player != null) {
						showList(sender, player, 1);
						return true;
					} else {
						sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl list [user] [page]");
						return false;
					}
				} else if (args.length == 3) {
					Player player = Bukkit.getPlayer(args[1]);
					if (StringUtils.isNumeric(args[2]) && player != null) {
						int page = Integer.parseInt(args[2]);
						showList(sender, player, page);
						return true;
					} else {
						sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl list [user] [page]");
						return false;
					}
				} else {
					sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl list [user] [page]");
					return false;
				}
			}
		} else if (args[0].equalsIgnoreCase("unload") && (sender.hasPermission(Permissions.UNLOAD) || sender.hasPermission(Permissions.UNLOAD_OTHER))) {
			if (args.length != 3) {
				if (Utilities.config.getBoolean("debug-messages")) {
					DirtLoader.getPlugin().getLogger().log(Level.SEVERE, "Invalid arguments for unload: " + Arrays.toString(args));
				}
				return false;
			}

			Player player = Bukkit.getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(Strings.PLAYER_NOT_FOUND);
				return false;
			}

			if (sender.hasPermission(Permissions.UNLOAD) && !sender.hasPermission(Permissions.UNLOAD_OTHER)) {
				if (!player.getName().equals(sender.getName())) {
					sender.sendMessage(Strings.NO_UNLOAD_OTHER_PERMS);
					return false;
				}

				Utilities.unloadChunkOfPlayer(sender, player, args[2]);
				return true;
			}

			Utilities.unloadChunkOfPlayer(sender, player, args[2]);
			return true;
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission(Permissions.RELOAD)) {
				sender.sendMessage(Strings.NO_PERMISSION);
				return false;
			}

			Utilities.reloadConfigFile();
			sender.sendMessage(Strings.CONFIG_RELOADED);
			return true;
		} else if (args[0].equalsIgnoreCase("info") && sender.hasPermission(Permissions.INFO)) {
			if (args.length != 1) {
				sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl info");
				return false;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(Strings.NO_CONSOLE);
				return false;
			}

			Player player = (Player) sender;
			Chunk currentChunk = player.getLocation().getChunk();
			int chunkX = currentChunk.getX();
			int chunkZ = currentChunk.getZ();
			String world = currentChunk.getWorld().getName();
			String chunkString = world + "#" + chunkX + "#" + chunkZ;

			showInfo(sender, chunkString);
		}

		return false;
	}

	private void showBal(CommandSender sender, Player player) {
		sender.sendMessage(Strings.BAR_TOP + ChatColor.GREEN + player.getName() + ChatColor.GRAY + "'s Balance:\n" + " " + ChatColor.DARK_AQUA + "\nOnline: " + ChatColor.GRAY + Utilities.getPlayerFile(player).getInt("used.online") + ChatColor.GOLD + " / " + ChatColor.GRAY + Utilities.getPlayerFile(player).getInt("available.online") + "\n");
		sender.sendMessage(ChatColor.DARK_AQUA + "Offline: " + ChatColor.GRAY + Utilities.getPlayerFile(player).getInt("used.offline") + ChatColor.GOLD + " / " + ChatColor.GRAY + Utilities.getPlayerFile(player).getInt("available.offline") + "\n" + Strings.BAR_BOTTOM);
	}

	private void showList(CommandSender sender, Player player, int page) {
		int totalUsed = Utilities.getPlayerFile(player).getInt("used.online") + Utilities.getPlayerFile(player).getInt("used.offline");
		if (totalUsed == 0) {
			sender.sendMessage(Strings.NO_CHUNKS_LOADED);
			return;
		}

		int maxPages = (int) Math.ceil((double) Utilities.getPlayerFile(player).getStringList("chunks").size() / (double) Utilities.config.getInt("general.max-list-entries-per-page"));
		if (page > maxPages) {
			sender.sendMessage(Strings.PAGE_INDEX_OUT_OF_BOUNDS + " Index must be smaller or equal to: " + maxPages);
			return;
		}

		int start = (page - 1) * Utilities.config.getInt("general.max-list-entries-per-page");
		int end = page * Utilities.config.getInt("general.max-list-entries-per-page");
		if (end > Utilities.getPlayerFile(player).getStringList("chunks").size()) {
			end = Utilities.getPlayerFile(player).getStringList("chunks").size();
		}
		sender.sendMessage(Strings.BAR_TOP);
		sender.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GRAY + "'s Chunks (" + ChatColor.DARK_AQUA + page + ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + maxPages + ChatColor.GRAY + "):");

		boolean permUnload = sender.hasPermission(Permissions.UNLOAD);
		boolean permUnloadOther = sender.hasPermission(Permissions.UNLOAD_OTHER);
		boolean permTeleport = sender.hasPermission(Permissions.TELEPORT);
		boolean senderEqualsPlayer = sender.getName().equals(player.getName());

		for (int i = start; i < end; i++) {
			if ((senderEqualsPlayer && permUnload) || (!senderEqualsPlayer && permUnloadOther)) {
				String[] chunkData = Utilities.getPlayerFile(player).getStringList("chunks").get(i).split("#");
				TextComponent entry = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "\u2715" + ChatColor.DARK_GRAY + "]");
				entry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl unload " + player.getName() + " " + Utilities.getPlayerFile(player).getStringList("chunks").get(i)));
				entry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY + "Unload chunk")));
				TextComponent chunkloaderPart = new TextComponent(ChatColor.GRAY + " - " + ChatColor.GOLD + "Chunkloader");
				if (!permTeleport) {
					chunkloaderPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
							ChatColor.GRAY + "Owner" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getName() + "\n" +
									"\n" +
									ChatColor.GRAY + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + chunkData[3] + "\n" +
									ChatColor.GRAY + "World" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + chunkData[0] + "\n" +
									ChatColor.GRAY + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Chunk " + ChatColor.DARK_AQUA + chunkData[1] + ChatColor.GOLD + " | " + ChatColor.DARK_AQUA + chunkData[2] + "\n" +
									ChatColor.GRAY + "Created" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + chunkData[4].split("_")[0] + ChatColor.GRAY + " at " + ChatColor.GOLD + chunkData[4].split("_")[1]
					)));
				} else {
					World world = Bukkit.getWorld(chunkData[0]);
					assert world != null;
					Chunk chunk = world.getChunkAt(Integer.parseInt(chunkData[1]), Integer.parseInt(chunkData[2]));
					int x = chunk.getBlock(7, 0, 7).getX();
					int z = chunk.getBlock(7, 0, 7).getZ();
					int y = world.getHighestBlockYAt(x, z) + 5;
					chunkloaderPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
							ChatColor.GRAY + "Owner" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getName() + "\n" +
									"\n" +
									ChatColor.GRAY + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + chunkData[3] + "\n" +
									ChatColor.GRAY + "World" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + chunkData[0] + "\n" +
									ChatColor.GRAY + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Chunk " + ChatColor.DARK_AQUA + chunkData[1] + ChatColor.GOLD + " | " + ChatColor.DARK_AQUA + chunkData[2] + "\n" +
									ChatColor.GRAY + "Created" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + chunkData[4].split("_")[0] + ChatColor.GRAY + " at " + ChatColor.GOLD + chunkData[4].split("_")[1] + "\n" +
									"\n" +
									ChatColor.DARK_AQUA + "Click to teleport to this chunk" + ChatColor.GRAY + "."
					)));
					chunkloaderPart.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Utilities.config.get("general.teleport-command") + " " + sender.getName() + " " + x + " " + y + " " + z));
				}
				entry.addExtra(chunkloaderPart);
				sender.spigot().sendMessage(entry);
			}
		}
		sender.sendMessage(Strings.BAR_BOTTOM);
	}

	private void showInfo(CommandSender sender, String chunkString) {
		File folder = new File(DirtLoader.getPlugin().getDataFolder() + "/playerdata/");
		File[] fileList = folder.listFiles();
		HashMap<String, String> loadersFound = new HashMap<>();

		if (fileList == null) {
			sender.sendMessage(ChatColor.RED + "No playerdata found!");
			return;
		}

		for (File file : fileList) {
			if (file.isFile()) {
				FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
				for (String chunk : playerFile.getStringList("chunks")) {
					if (chunk.contains(chunkString)) {
						loadersFound.put(playerFile.getString("name"), chunk);
					}
				}
			}
		}

		if (loadersFound.isEmpty()) {
			sender.sendMessage(Strings.NO_LOADERS_FOUND_IN_CHUNK);
			return;
		}

		sender.sendMessage(Strings.BAR_TOP);
		sender.sendMessage(ChatColor.GRAY + "Chunk ( " + ChatColor.DARK_AQUA + chunkString.split("#")[1] + ChatColor.GOLD + " | " + ChatColor.DARK_AQUA + chunkString.split("#")[2] + ChatColor.GRAY + " ) is loaded by:");


		for (Map.Entry<String, String> entry : loadersFound.entrySet()) {
			TextComponent foundLoader = null;
			if (sender.hasPermission(Permissions.UNLOAD_OTHER)) {
				foundLoader = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "\u2715" + ChatColor.DARK_GRAY + "]");
				foundLoader.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl unload " + entry.getKey() + " " + entry.getValue()));
				foundLoader.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY + "Unload chunk")));
			}

			TextComponent chunkloaderPart = new TextComponent(ChatColor.GRAY + " - " + ChatColor.GOLD + entry.getKey());
			chunkloaderPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
					ChatColor.GRAY + "Owner" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + entry.getKey() + "\n" +
							"\n" +
							ChatColor.GRAY + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + entry.getValue().split("#")[3] + "\n" +
							ChatColor.GRAY + "World" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + entry.getValue().split("#")[0] + "\n" +
							ChatColor.GRAY + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Chunk " + ChatColor.DARK_AQUA + entry.getValue().split("#")[1] + ChatColor.GOLD + " | " + ChatColor.DARK_AQUA + entry.getValue().split("#")[2] + "\n" +
							ChatColor.GRAY + "Created" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + entry.getValue().split("#")[4].split("_")[0] + ChatColor.GRAY + " at " + ChatColor.GOLD + entry.getValue().split("#")[4].split("_")[1]
			)));
			chunkloaderPart.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ""));

			if (foundLoader == null) {
				sender.spigot().sendMessage(chunkloaderPart);
			} else {
				foundLoader.addExtra(chunkloaderPart);
				sender.spigot().sendMessage(foundLoader);
			}

			sender.sendMessage(Strings.BAR_BOTTOM);
		}
	}

	private ArrayList<String> getListings(CommandSender sender) {
		ArrayList<String> listings = new ArrayList<>();
		if (sender.hasPermission(Permissions.LIST_USER)) {
			listings.add(Strings.HELP_LIST_USER);
		}

		if (sender.hasPermission(Permissions.LIST) && !sender.hasPermission(Permissions.LIST_USER)) {
			listings.add(Strings.HELP_LIST);
		}

		if (sender.hasPermission(Permissions.BAL_USER)) {
			listings.add(Strings.HELP_BAL_USER);
		}

		if (sender.hasPermission(Permissions.BAL) && !sender.hasPermission(Permissions.BAL_USER)) {
			listings.add(Strings.HELP_BAL);
		}

		if (sender.hasPermission(Permissions.CHUNKS)) {
			listings.add(Strings.HELP_CHUNKS);
		}

		if (sender.hasPermission(Permissions.RELOAD)) {
			listings.add(Strings.HELP_RELOAD);
		}

		if (sender.hasPermission(Permissions.INFO)) {
			listings.add(Strings.HELP_INFO);
		}

		if (sender.hasPermission(Permissions.LOAD)) {
			listings.add(Strings.LOAD);
		}

		return listings;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> arguments = new ArrayList<>();

		if (args.length == 1) {
			if (sender.hasPermission(Permissions.LIST_USER)) {
				arguments.add("list");
			}

			if (sender.hasPermission(Permissions.LOAD)) {
				arguments.add("help");
			}

			if (sender.hasPermission(Permissions.BAL_USER) || sender.hasPermission(Permissions.BAL)) {
				arguments.add("bal");
			}

			if (sender.hasPermission(Permissions.CHUNKS)) {
				arguments.add("chunks");
			}

			if (sender.hasPermission(Permissions.RELOAD)) {
				arguments.add("reload");
			}

			if (sender.hasPermission(Permissions.INFO)) {
				arguments.add("info");
			}

			if (sender.hasPermission(Permissions.LOAD)) {
				arguments.add("load");
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("list") && sender.hasPermission(Permissions.LIST_USER)) {
			for (Player player : sender.getServer().getOnlinePlayers()) {
				arguments.add(player.getName());
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("bal") && sender.hasPermission(Permissions.BAL_USER)) {
			for (Player player : sender.getServer().getOnlinePlayers()) {
				arguments.add(player.getName());
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("load") && sender.hasPermission(Permissions.LOAD)) {
			arguments.add("online");
			arguments.add("offline");
		} else if (args.length == 2 && args[0].equalsIgnoreCase("chunks") && sender.hasPermission(Permissions.CHUNKS)) {
			arguments.add("add");
			arguments.add("remove");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("chunks") && sender.hasPermission(Permissions.CHUNKS)) {
			for (Player player : sender.getServer().getOnlinePlayers()) {
				arguments.add(player.getName());
			}
		} else if (args.length == 4 && args[0].equalsIgnoreCase("chunks") && sender.hasPermission(Permissions.CHUNKS)) {
			arguments.add("online");
			arguments.add("offline");
		}

		List<String> tabResults = new ArrayList<>();
		for (String argument : arguments) {
			if (argument.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
				tabResults.add(argument);
			}
		}

		return tabResults;
	}
}
