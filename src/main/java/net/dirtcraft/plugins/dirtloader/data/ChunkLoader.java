package net.dirtcraft.plugins.dirtloader.data;

import net.dirtcraft.plugins.dirtloader.utils.Utilities;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChunkLoader {
	private final UUID uuid;
	private final String type;
	private final UUID ownerUuid;
	private final LocalDateTime creationTime;
	private LocalDateTime shutdownTime;
	private final Chunk chunk;

	public ChunkLoader(UUID ownerUuid, Chunk chunk, String type) {
		this.uuid = UUID.randomUUID();
		this.ownerUuid = ownerUuid;
		this.chunk = chunk;
		this.type = type;
		this.creationTime = LocalDateTime.now();
		this.shutdownTime = LocalDateTime.now().plusHours(Utilities.config.offlineLoader.offlineLoaderDuration);
	}

	public ChunkLoader(UUID uuid, UUID ownerUuid, Chunk chunk, String type, LocalDateTime creationTime, LocalDateTime shutdownTime) {
		this.uuid = uuid;
		this.ownerUuid = ownerUuid;
		this.chunk = chunk;
		this.type = type;
		this.creationTime = creationTime;
		this.shutdownTime = shutdownTime;
	}

	public String getType() {
		return type;
	}

	public UUID getOwnerUuid() {
		return ownerUuid;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public LocalDateTime getShutdownTime() {
		return shutdownTime;
	}
	public void setShutdownTime(LocalDateTime shutdownTime) {
		this.shutdownTime = shutdownTime;
	}
}
