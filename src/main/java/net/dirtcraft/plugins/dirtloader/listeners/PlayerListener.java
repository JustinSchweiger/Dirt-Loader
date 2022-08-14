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
		updatePlayerShutdownTime(uuid, name);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		String name = e.getPlayer().getName();
		updatePlayerShutdownTime(uuid, name);
		handleUnloadingOfChunks(uuid);
	}

	private static void updatePlayerShutdownTime(UUID uuid, String name) {
		DatabaseOperation.updatePlayerShutdownTime(uuid, shutdownTime -> {
			if (Utilities.config.general.debug) {
				Utilities.log(Level.INFO, "Updated " + name + "'s shutdown time to " + shutdownTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
			}
		});
	}

	private static void loadAllChunksOfPlayer(UUID uuid) {
		DatabaseOperation.getPlayer("player_uuid", uuid.toString(), new PlayerCallback() {
			@Override
			public void onPlayerFound(Player player) {
				for (ChunkLoader chunkloader : player.getChunkLoaders()) {
					ChunkManager.addChunk(player.getUuid(), chunkloader);
				}

				if (Utilities.config.general.debug) {
					Utilities.log(Level.INFO, "Loading " + player.getChunkLoaders().size() + " chunks for player " + player.getName());
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
				0,
				LocalDateTime.now().plusHours(Utilities.config.offlineLoader.offlineLoaderDuration)
		);
		DatabaseOperation.insertPlayer(player);
	}

	private static void handleUnloadingOfChunks(UUID uuid) {
		List<ChunkLoader> chunkloaders = ChunkManager.getChunkloadersOfPlayer(uuid);
		System.out.println(chunkloaders);

		for (ChunkLoader chunkloader : chunkloaders) {
			System.out.println("called in loop");
			boolean unloadChunk = !isSameChunkLoadedBySomeOneElse(chunkloader.getChunk(), uuid);
			System.out.println(unloadChunk);
			if (unloadChunk) {
				if (!chunkloader.getType().equalsIgnoreCase("offline")) {
					ChunkManager.removeChunk(uuid, chunkloader);
				}
			}
		}
	}

	private static boolean isSameChunkLoadedBySomeOneElse(Chunk chunk, UUID uuid) {
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
