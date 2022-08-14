package net.dirtcraft.plugins.dirtloader.data;

import net.dirtcraft.plugins.dirtloader.DirtLoader;
import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.database.callbacks.OfflineChunkloaderCallback;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

import static net.dirtcraft.plugins.dirtloader.listeners.PlayerListener.isSameChunkLoadedBySomeOneElse;

public class ChunkManager {
	private static BukkitScheduler scheduler;
	private static HashMap<UUID, List<ChunkLoader>> loadedChunks;

	/**
	 * Initializes the chunk manager.
	 */
	public static void init() {
		loadedChunks = new HashMap<>();
		scheduler = Bukkit.getScheduler();
	}

	/**
	 * Returns all the loaded Chunks in a HashMap with the UUID of the owner as the key and a List of Instances of ChunkLoader
	 *
	 * @return The HashMap containing all the loaded Chunks
	 */
	public static HashMap<UUID, List<ChunkLoader>> getLoadedChunks() {
		return loadedChunks;
	}

	/**
	 * Starts the offline chunkloader purge task. It first runs after 30s and then the delay specified in the config is used.
	 */
	public static void startOfflinePurgeTask() {
		scheduler.runTaskTimer(DirtLoader.getPlugin(), () -> {
			List<ChunkLoader> offlineLoadedChunks = getAllOfflineLoaders();
			if (offlineLoadedChunks.size() == 0) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.INFO, "No offline loaded chunks to purge found.");
				}
			}

			for (ChunkLoader chunkLoader : offlineLoadedChunks) {
				if (chunkLoader.getShutdownTime().isBefore(LocalDateTime.now())) {
					if (!Utilities.isPlayerOnline(chunkLoader.getOwnerUuid())) {
						if (Utilities.config.general.debug) {
							Utilities.log(Level.INFO, Strings.PURGE_OFFLINE_LOADER.replace("{X}", Integer.toString(chunkLoader.getChunk().getX())).replace("{Z}", Integer.toString(chunkLoader.getChunk().getZ())));
						}
						boolean unloadChunk = !isSameChunkLoadedBySomeOneElse(chunkLoader.getChunk(), chunkLoader.getOwnerUuid());
						System.out.println(unloadChunk);
						if (unloadChunk) {
							ChunkManager.removeChunk(chunkLoader.getOwnerUuid(), chunkLoader);
						}
					} else {
						if (Utilities.config.general.debug) {
							Utilities.log(Level.INFO, Strings.NO_PURGE_BECAUSE_ONLINE.replace("{X}", Integer.toString(chunkLoader.getChunk().getX())).replace("{Z}", Integer.toString(chunkLoader.getChunk().getZ())));
						}
					}
				}
			}
		}, 20L * 30L, 20L * 60L * Long.parseLong(String.valueOf(Utilities.config.general.offlineLoaderCheckInterval)));
	}

	/**
	 * Restarts the offline chunkloader purge task.
	 */
	public static void restartOfflinePurgeTask() {
		scheduler.cancelTasks(DirtLoader.getPlugin());
		startOfflinePurgeTask();
	}

	/**
	 * Gets a list of all currently loaded Chunkloaders.
	 *
	 * @param uuid The UUID of the player.
	 *
	 * @return A list of all currently loaded Chunkloaders.
	 */
	public static List<ChunkLoader> getAllOtherLoadedChunkloaders(UUID uuid) {
		List<ChunkLoader> allLoadedChunks = new ArrayList<>();
		for (Map.Entry<UUID, List<ChunkLoader>> chunkLoaders : loadedChunks.entrySet()) {
			if (!chunkLoaders.getKey().equals(uuid)) {
				allLoadedChunks.addAll(chunkLoaders.getValue());
			}
		}
		return allLoadedChunks;
	}

	/**
	 * Gets a list of all currently loaded offline Chunkloaders.
	 *
	 * @return A list of all currently loaded offline Chunkloaders.
	 */
	public static List<ChunkLoader> getAllOfflineLoaders() {
		List<ChunkLoader> allOfflineLoadedChunks = new ArrayList<>();
		for (Map.Entry<UUID, List<ChunkLoader>> chunkLoaders : loadedChunks.entrySet()) {
			for (ChunkLoader chunkLoader : chunkLoaders.getValue()) {
				if (chunkLoader.getType().equalsIgnoreCase("offline")) {
					allOfflineLoadedChunks.add(chunkLoader);
				}
			}
		}

		return allOfflineLoadedChunks;
	}

	/**
	 * Adds a chunkloader to the list of loaded chunks.
	 *
	 * @param uuid        The UUID of the player who owns the chunkloader.
	 * @param chunkLoader The chunkloader to add.
	 */
	public static void addChunk(UUID uuid, ChunkLoader chunkLoader) {
		if (!isChunkLoaded(chunkLoader.getChunk())) {
			loadChunk(chunkLoader.getChunk());
			if (Utilities.config.general.logDebugCoords) {
				Utilities.log(Level.INFO, "Loading chunk (" + chunkLoader.getChunk().getX() + " | " + chunkLoader.getChunk().getZ() + ")");
			}
		}

		if (!loadedChunks.containsKey(uuid)) {
			loadedChunks.put(uuid, new ArrayList<>());
		}
		loadedChunks.get(uuid).add(chunkLoader);
	}

	/**
	 * Removes a chunkloader from the loaded chunks list.
	 *
	 * @param uuid        The UUID of the player who owns the chunkloader.
	 * @param chunkLoader The chunkloader to be removed.
	 */
	public static void removeChunk(UUID uuid, ChunkLoader chunkLoader) {
		if (loadedChunks.containsKey(uuid)) {
			loadedChunks.get(uuid).remove(chunkLoader);
		}

		unloadChunk(chunkLoader.getChunk());
		if (Utilities.config.general.logDebugCoords) {
			Utilities.log(Level.INFO, "Unloading chunk (" + chunkLoader.getChunk().getX() + " | " + chunkLoader.getChunk().getZ() + ")");
		}
	}

	public static void removeChunkWithoutUnload(UUID uuid, ChunkLoader chunkLoader) {
		if (loadedChunks.containsKey(uuid)) {
			loadedChunks.get(uuid).remove(chunkLoader);
		}
	}

	/**
	 * Checks if a chunk is loaded in the world.
	 *
	 * @param chunk The chunk to be checked.
	 *
	 * @return True if the chunk is loaded, false otherwise.
	 */
	public static boolean isChunkLoaded(Chunk chunk) {
		for (List<ChunkLoader> chunkLoaders : loadedChunks.values()) {
			for (ChunkLoader chunkLoader : chunkLoaders) {
				if (chunkLoader.getChunk().getWorld().equals(chunk.getWorld()) && chunkLoader.getChunk().getX() == chunk.getX() && chunkLoader.getChunk().getZ() == chunk.getZ()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if a player has a specific chunk loaded.
	 *
	 * @param uuid  The UUID of the player.
	 * @param chunk The chunk to be checked.
	 *
	 * @return True if the player has the chunk loaded, false otherwise.
	 */
	public static boolean isChunkLoaded(UUID uuid, Chunk chunk) {
		if (loadedChunks.containsKey(uuid)) {
			for (ChunkLoader chunkLoader : loadedChunks.get(uuid)) {
				if (chunkLoader.getChunk().getWorld().equals(chunk.getWorld()) && chunkLoader.getChunk().getX() == chunk.getX() && chunkLoader.getChunk().getZ() == chunk.getZ()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets a list of all loaded chunks of a specific player.
	 *
	 * @param uuid The UUID of the player.
	 *
	 * @return A list of all loaded chunks of the player.
	 */
	public static List<ChunkLoader> getChunkloadersOfPlayer(UUID uuid) {
		if (loadedChunks.containsKey(uuid)) {
			return loadedChunks.get(uuid);
		}
		return new ArrayList<>();
	}

	public static void loadActiveOfflineChunks() {
		DatabaseOperation.getOfflineChunkloaders(chunkloaders -> {
			if (chunkloaders.size() == 0) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.INFO, "No offline chunkloaders to load found.");
				}
				return;
			}

			for (ChunkLoader chunkLoader : chunkloaders) {
				if (chunkLoader.getShutdownTime().isAfter(LocalDateTime.now())) {
					addChunk(chunkLoader.getOwnerUuid(), chunkLoader);
				}
			}
		});
	}

	public static void updateShutdownTime(UUID uuid, LocalDateTime shutdownTime) {
		if (loadedChunks.containsKey(uuid)) {
			for (ChunkLoader chunkLoader : loadedChunks.get(uuid)) {
				chunkLoader.setShutdownTime(shutdownTime);
			}
		}
	}

	private static void loadChunk(Chunk chunk) {
		DirtLoader.getPlugin().getServer().getWorld(chunk.getWorld()).addPluginChunkTicket(chunk.getX(), chunk.getZ(), DirtLoader.getPlugin());
	}

	private static void unloadChunk(Chunk chunk) {
		DirtLoader.getPlugin().getServer().getWorld(chunk.getWorld()).removePluginChunkTicket(chunk.getX(), chunk.getZ(), DirtLoader.getPlugin());
	}
}
