package net.dirtcraft.plugins.dirtloader.data;

import net.dirtcraft.plugins.dirtloader.DirtLoader;

import java.util.*;

public class ChunkManager {
	private static HashMap<UUID, List<ChunkLoader>> loadedChunks;

	public static void init() {
		loadedChunks = new HashMap<>();
	}

	public static HashMap<UUID, List<ChunkLoader>> getLoadedChunks() {
		return loadedChunks;
	}

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
	 * Adds a chunkloader to the list of loaded chunks.
	 * @param uuid The UUID of the player who owns the chunkloader.
	 * @param chunkLoader The chunkloader to add.
	 */
	public static void addChunk(UUID uuid, ChunkLoader chunkLoader) {
		if (!isChunkLoaded(chunkLoader.getChunk())) {
			loadChunk(chunkLoader.getChunk());
			System.out.println("Loading chunk " + chunkLoader.getChunk().getX() + " " + chunkLoader.getChunk().getZ());
		}

		if (!loadedChunks.containsKey(uuid)) {
			loadedChunks.put(uuid, new ArrayList<>());
		}
		loadedChunks.get(uuid).add(chunkLoader);
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

		unloadChunk(chunkLoader.getChunk());
		System.out.println("Unloading chunk " + chunkLoader.getChunk().getX() + " " + chunkLoader.getChunk().getZ());
	}

	/**
	 * Checks if a chunk is loaded in the world.
	 * @param chunk The chunk to be checked.
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
	 * @param uuid The UUID of the player.
	 * @param chunk The chunk to be checked.
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
	 * @param uuid The UUID of the player.
	 * @return A list of all loaded chunks of the player.
	 */
	public static List<ChunkLoader> getChunkloadersOfPlayer(UUID uuid) {
		if (loadedChunks.containsKey(uuid)) {
			return loadedChunks.get(uuid);
		}
		return new ArrayList<>();
	}

	private static void loadChunk(Chunk chunk) {
		DirtLoader.getPlugin().getServer().getWorld(chunk.getWorld()).addPluginChunkTicket(chunk.getX(), chunk.getZ(), DirtLoader.getPlugin());
	}

	private static void unloadChunk(Chunk chunk) {
		DirtLoader.getPlugin().getServer().getWorld(chunk.getWorld()).removePluginChunkTicket(chunk.getX(), chunk.getZ(), DirtLoader.getPlugin());
	}
}
