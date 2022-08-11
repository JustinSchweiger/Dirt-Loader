package net.dirtcraft.plugins.dirtloader;

import net.dirtcraft.plugins.dirtloader.commands.BaseCommand;
import net.dirtcraft.plugins.dirtloader.listeners.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class DirtLoader extends JavaPlugin {

	public static DirtLoader plugin;

	@Override
	public void onEnable() {
		plugin = this;
		Utilities.createConfigFile();
		Utilities.registerListener();
		Utilities.registerCommands();
	}

	@Override
	public void onDisable() {

	}
}
