package net.dirtcraft.plugins.dirtloader;

import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import net.dirtcraft.plugins.dirtloader.database.Database;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.plugin.java.JavaPlugin;

public final class DirtLoader extends JavaPlugin {
	private static DirtLoader plugin;

	public static DirtLoader getPlugin() {
		return plugin;
	}

	@Override
	public void onEnable() {
		plugin = this;
		Utilities.loadConfig();
		Database.initialiseDatabase();
		ChunkManager.init();
		ChunkManager.loadActiveOfflineChunks();
		ChunkManager.startOfflinePurgeTask();
		Utilities.registerListener();
		Utilities.registerCommands();
	}

	@Override
	public void onDisable() {
		Database.closeDatabase();
	}
}
