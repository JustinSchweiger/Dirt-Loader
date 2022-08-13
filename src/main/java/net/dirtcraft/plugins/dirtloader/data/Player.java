package net.dirtcraft.plugins.dirtloader.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {
	private final UUID uuid;
	private final String name;
	private final int onlineAvailable;
	private final int offlineAvailable;
	private final int onlineUsed;
	private final int offlineUsed;
	private final LocalDateTime shutdownTime;
	private final List<ChunkLoader> chunkLoaders;

	public Player(UUID uuid, String name, int onlineAvailable, int offlineAvailable, int onlineUsed, int offlineUsed, LocalDateTime shutdownTime) {
		this.uuid = uuid;
		this.name = name;
		this.onlineAvailable = onlineAvailable;
		this.offlineAvailable = offlineAvailable;
		this.onlineUsed = onlineUsed;
		this.offlineUsed = offlineUsed;
		this.shutdownTime = shutdownTime;
		this.chunkLoaders = new ArrayList<>();
	}

	public Player(UUID uuid, String name, int onlineAvailable, int offlineAvailable, int onlineUsed, int offlineUsed, LocalDateTime shutdownTime, List<ChunkLoader> chunkLoaders) {
		this.uuid = uuid;
		this.name = name;
		this.onlineAvailable = onlineAvailable;
		this.offlineAvailable = offlineAvailable;
		this.onlineUsed = onlineUsed;
		this.offlineUsed = offlineUsed;
		this.shutdownTime = shutdownTime;
		this.chunkLoaders = chunkLoaders;
	}

	public int getOfflineUsed() {
		return offlineUsed;
	}

	public int getOnlineUsed() {
		return onlineUsed;
	}

	public int getOfflineAvailable() {
		return offlineAvailable;
	}

	public int getOnlineAvailable() {
		return onlineAvailable;
	}

	public String getName() {
		return name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public List<ChunkLoader> getChunkLoaders() {
		return chunkLoaders;
	}

	public LocalDateTime getShutdownTime() {
		return shutdownTime;
	}
}
