package net.dirtcraft.plugins.dirtloader.utils;

import com.moandjiezana.toml.Toml;
import net.dirtcraft.plugins.dirtloader.DirtLoader;
import net.dirtcraft.plugins.dirtloader.commands.BaseCommand;
import net.dirtcraft.plugins.dirtloader.config.Config;
import net.dirtcraft.plugins.dirtloader.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

public class Utilities {
	public static Config config;
	private static BukkitTask offlineChunkloaderScheduler;

	public static String format(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	static void startOfflineChunkloaderScheduler() {
		// Check every 10 minutes if there are offline chunks that need to be unloaded. Start after 30s.
		//offlineChunkloaderScheduler = Bukkit.getScheduler().runTaskTimer(DirtLoader.getPlugin(), ChunkManager::unloadOverdueOfflineChunkloaders, 20L * 30L, 20L * 60L * config.general.offlineLoaderCheckInterval);
	}

	public static void loadConfig() {
		if (!DirtLoader.getPlugin().getDataFolder().exists()) {
			DirtLoader.getPlugin().getDataFolder().mkdirs();
		}
		File file = new File(DirtLoader.getPlugin().getDataFolder(), "config.toml");
		if (!file.exists()) {
			try {
				Files.copy(DirtLoader.getPlugin().getResource("config.toml"), file.toPath());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		config = new Toml(new Toml().read(DirtLoader.getPlugin().getResource("config.toml"))).read(file).to(Config.class);
	}

	public static void registerListener() {
		DirtLoader.getPlugin().getServer().getPluginManager().registerEvents(new PlayerListener(), DirtLoader.getPlugin());
	}

	public static void registerCommands() {
		DirtLoader.getPlugin().getCommand("dl").setExecutor(new BaseCommand());
		DirtLoader.getPlugin().getCommand("dl").setTabCompleter(new BaseCommand());
	}

	public static void log(Level level, String msg) {
		String consoleMessage;
		if (Level.INFO.equals(level)) {
			consoleMessage = Strings.INTERNAL_PREFIX + ChatColor.WHITE + msg;
		} else if (Level.WARNING.equals(level)) {
			consoleMessage = Strings.INTERNAL_PREFIX + ChatColor.YELLOW + msg;
		} else if (Level.SEVERE.equals(level)) {
			consoleMessage = Strings.INTERNAL_PREFIX + ChatColor.RED + msg;
		} else {
			consoleMessage = Strings.INTERNAL_PREFIX + ChatColor.GRAY + msg;
		}

		if (!config.general.coloredDebug) {
			consoleMessage = ChatColor.stripColor(msg);
		}

		DirtLoader.getPlugin().getServer().getConsoleSender().sendMessage(consoleMessage);
	}

	public static void disablePlugin() {
		DirtLoader.getPlugin().getServer().getPluginManager().disablePlugin(DirtLoader.getPlugin());
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;
	}

	public static void playSuccessSound(CommandSender sender) {
		Player player = (Player) sender;
		if (Utilities.config.sound.playSuccessSound) {
			String sound = Utilities.config.sound.successSound;
			if (sound == null) {
				sound = "minecraft:entity.experience_orb.pickup";
			}
			player.playSound(player.getLocation(), sound, 1, 1);
		}
	}

	public static void playErrorSound(CommandSender sender) {
		Player player = (Player) sender;
		if (Utilities.config.sound.playErrorSound) {
			String sound = Utilities.config.sound.errorSound;
			if (sound == null) {
				sound = "minecraft:entity.creeper.death";
			}
			player.playSound(player.getLocation(), sound, 1, 1);
		}
	}

	public static void playUnloadSound(CommandSender sender) {
		Player player = (Player) sender;
		if (Utilities.config.sound.playUnloadSound) {
			String sound = Utilities.config.sound.unloadSound;
			if (sound == null) {
				sound = "minecraft:block.beacon.deactivate";
			}
			player.playSound(player.getLocation(), sound, 1, 1);
		}
	}
}
