package net.dirtcraft.plugins.dirtloader;

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
