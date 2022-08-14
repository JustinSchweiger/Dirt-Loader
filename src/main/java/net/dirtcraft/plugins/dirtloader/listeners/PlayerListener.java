package net.dirtcraft.plugins.dirtloader.listeners;

import net.dirtcraft.plugins.dirtloader.data.Chunk;
import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;
import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import net.dirtcraft.plugins.dirtloader.data.Player;
import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.database.callbacks.PlayerCallback;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;

public class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		String name = e.getPlayer().getName();
		addPossibleNewPlayerToDatabase(uuid, name);
		loadAllChunksOfPlayer(uuid);
		updateShutdownTime(uuid, name);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		String name = e.getPlayer().getName();
		updateShutdownTime(uuid, name);
		handleUnloadingOfChunks(uuid);
	}

	private static void updateShutdownTime(UUID uuid, String name) {
		DatabaseOperation.updateShutdownTime(uuid, (shutdownTime) -> {
			if (Utilities.config.general.debug) {
				Utilities.log(Level.INFO, "Updated " + name + "'s shutdown time to " + shutdownTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
			}

			ChunkManager.updateShutdownTime(uuid, shutdownTime);
		});
	}

	private static void loadAllChunksOfPlayer(UUID uuid) {
		DatabaseOperation.getPlayer("player_uuid", uuid.toString(), new PlayerCallback() {
			@Override
			public void onPlayerFound(Player player) {
				int onlineLoaded = 0;
				int offlineLoaded = 0;

				for (ChunkLoader chunkloader : player.getChunkLoaders()) {
					if (chunkloader.getType().equalsIgnoreCase("online")) {
						ChunkManager.addChunk(player.getUuid(), chunkloader);
						onlineLoaded++;
					} else if (chunkloader.getShutdownTime().isBefore(LocalDateTime.now())) {
						ChunkManager.addChunk(player.getUuid(), chunkloader);
						offlineLoaded++;
					}
				}

				if (Utilities.config.general.debug) {
					Utilities.log(Level.INFO, "Loading " + onlineLoaded + " online chunks for player " + player.getName());
					Utilities.log(Level.INFO, "Loading " + offlineLoaded + " offline chunks for player " + player.getName());
				}
			}

			@Override
			public void onPlayerNotFound(String value) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.WARNING, "No results found for " + value);
				}
			}
		});
	}

	private static void addPossibleNewPlayerToDatabase(UUID uuid, String name) {
		Player player = new Player(
				uuid,
				name,
				Utilities.config.general.onlineLoaderAmount,
				Utilities.config.general.offlineLoaderAmount,
				0,
				0
		);
		DatabaseOperation.insertPlayer(player);
	}

	private static void handleUnloadingOfChunks(UUID uuid) {
		List<ChunkLoader> chunkloaders = new ArrayList<>(ChunkManager.getChunkloadersOfPlayer(uuid));

		for (ChunkLoader chunkloader : chunkloaders) {
			boolean unloadChunk = !isSameChunkLoadedBySomeOneElse(chunkloader.getChunk(), uuid);
			if (unloadChunk) {
				if (!chunkloader.getType().equalsIgnoreCase("offline")) {
					ChunkManager.removeChunk(uuid, chunkloader);
				}
			} else {
				if (!chunkloader.getType().equalsIgnoreCase("offline")) {
					ChunkManager.removeChunkWithoutUnload(uuid, chunkloader);
				}
			}
		}
	}

	public static boolean isSameChunkLoadedBySomeOneElse(Chunk chunk, UUID uuid) {
		Optional<UUID> o = ChunkManager.getAllOtherLoadedChunkloaders(uuid)
				.stream()
				.filter(
						chunkLoader -> chunkLoader.getChunk().getWorld().equals(chunk.getWorld()) &&
								chunkLoader.getChunk().getX() == chunk.getX() &&
								chunkLoader.getChunk().getZ() == chunk.getZ()
				)
				.map(ChunkLoader::getOwnerUuid)
				.findFirst();

		return o.isPresent();
	}
}
