package net.dirtcraft.plugins.dirtloader;

import net.dirtcraft.plugins.dirtloader.commands.BaseCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class DirtLoader extends JavaPlugin {

	private static Plugin plugin;

	@Override
	public void onEnable() {
		plugin = this;
		Utilities.createConfigFile();
		getCommand("dl").setExecutor(new BaseCommand());
		getCommand("dl").setTabCompleter(new BaseCommand());
	}

	@Override
	public void onDisable() {

	}

	public static Plugin getPlugin() {
		return plugin;
	}
}
