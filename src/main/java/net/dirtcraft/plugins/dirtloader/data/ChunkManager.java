package net.dirtcraft.plugins.dirtloader.data;

import net.dirtcraft.plugins.dirtloader.DirtLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChunkManager {
	private static HashMap<UUID, List<ChunkLoader>> loadedChunks;

	public static void init() {
		loadedChunks = new HashMap<UUID, List<ChunkLoader>>();
	}

	/**
	 * Get the map of all loaded chunks.
	 * @return
	 */
	public static HashMap<UUID, List<ChunkLoader>> getLoadedChunks() {
		return loadedChunks;
	}

	/**
	 * Adds a chunkloader to the list of loaded chunks.
	 * @param uuid The UUID of the player who owns the chunkloader.
	 * @param chunkLoader The chunkloader to add.
	 */
	public static void addChunk(UUID uuid, ChunkLoader chunkLoader) {
		if (!loadedChunks.containsKey(uuid)) {
			loadedChunks.put(uuid, new ArrayList<>());
		}
		loadedChunks.get(uuid).add(chunkLoader);

		if (!isChunkLoaded(chunkLoader.getChunk())) {
			//loadChunk(chunkLoader.getChunk());
			System.out.println("Loading chunk " + chunkLoader.getChunk().getX() + " " + chunkLoader.getChunk().getZ());
		}
	}

	/**
	 * Removes a chunkloader from the loaded chunks list.
	 * @param uuid The UUID of the player who owns the chunkloader.
	 * @param chunkLoader The chunkloader to be removed.
	 */
	public static void removeChunk(UUID uuid, ChunkLoader chunkLoader) {
		if (loadedChunks.containsKey(uuid)) {
			loadedChunks.get(uuid).remove(chunkLoader);
		}
	}

	/**
	 * Checks if a chunk is loaded in the world.
	 * @param chunk The chunk to be checked.
	 * @return True if the chunk is loaded, false otherwise.
	 */
	public static boolean isChunkLoaded(Chunk chunk) {
		for (List<ChunkLoader> chunkLoaders : loadedChunks.values()) {
			for (ChunkLoader chunkLoader : chunkLoaders) {
				if (chunkLoader.getChunk().getWorld().getName().equals(chunk.getWorld().getName()) && chunkLoader.getChunk().getX() == chunk.getX() && chunkLoader.getChunk().getZ() == chunk.getZ()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if a player has a specific chunk loaded.
	 * @param uuid The UUID of the player.
	 * @param chunk The chunk to be checked.
	 * @return True if the player has the chunk loaded, false otherwise.
	 */
	public static boolean isChunkLoaded(UUID uuid, Chunk chunk) {
		if (loadedChunks.containsKey(uuid)) {
			for (ChunkLoader chunkLoader : loadedChunks.get(uuid)) {
				if (chunkLoader.getChunk().getWorld().getName().equals(chunk.getWorld().getName()) && chunkLoader.getChunk().getX() == chunk.getX() && chunkLoader.getChunk().getZ() == chunk.getZ()) {
					return true;
				}
			}
		}
		return false;
	}

	private static void loadChunk(Chunk chunk) {
		DirtLoader.getPlugin().getServer().getWorld(chunk.getWorld().getName()).addPluginChunkTicket(chunk.getX(), chunk.getZ(), DirtLoader.getPlugin());
	}

	private static void unloadChunk(Chunk chunk) {
		DirtLoader.getPlugin().getServer().getWorld(chunk.getWorld().getName()).removePluginChunkTicket(chunk.getX(), chunk.getZ(), DirtLoader.getPlugin());
	}
}
