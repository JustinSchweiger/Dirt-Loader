package net.dirtcraft.plugins.dirtloader.listeners;

import net.dirtcraft.plugins.dirtloader.data.Player;
import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = new Player(
				e.getPlayer().getUniqueId(),
				e.getPlayer().getName(),
				Utilities.config.general.onlineLoaderAmount,
				Utilities.config.general.offlineLoaderAmount,
				0,
				0,
				LocalDateTime.now().plusHours(Utilities.config.offlineLoader.offlineLoaderDuration)
		);
		DatabaseOperation.insertPlayer(player);

		/*
		Player player = e.getPlayer();
		ChunkManager.loadAllChunks(player, "online");

		int hoursUntilUnload = Utilities.config.getInt("offline.duration");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime unloadTime = now.plusHours(hoursUntilUnload);
		FileConfiguration playerFile = Utilities.getPlayerFile(player);

		if (!playerFile.getBoolean("offline.unloaded")) {
			ChunkManager.loadAllChunks(player, "offline");
		}

		playerFile.set("offline.shutdown", unloadTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm:ss")));
		playerFile.set("offline.unloaded", false);
		Utilities.savePlayerFile(playerFile, player.getUniqueId());*/
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		/*Player player = e.getPlayer();
		ChunkManager.unloadAllChunks(player);

		int hoursUntilUnload = Utilities.config.getInt("offline.duration");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime unloadTime = now.plusHours(hoursUntilUnload);
		FileConfiguration playerFile = Utilities.getPlayerFile(player);
		playerFile.set("offline.shutdown", unloadTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm:ss")));
		playerFile.set("offline.unloaded", false);
		Utilities.savePlayerFile(playerFile, player.getUniqueId());*/
	}
}
