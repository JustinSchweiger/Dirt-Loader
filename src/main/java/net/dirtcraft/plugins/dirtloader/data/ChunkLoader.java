package net.dirtcraft.plugins.dirtloader.data;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChunkLoader {
	private final UUID uuid;
	private final String type;
	private final UUID ownerUuid;
	private final LocalDateTime creationTime;
	private final Chunk chunk;

	public ChunkLoader(UUID ownerUuid, Chunk chunk, String type) {
		this.uuid = UUID.randomUUID();
		this.ownerUuid = ownerUuid;
		this.chunk = chunk;
		this.type = type;
		this.creationTime = LocalDateTime.now();
	}

	public ChunkLoader(UUID uuid, UUID ownerUuid, Chunk chunk, String type, LocalDateTime creationTime) {
		this.uuid = uuid;
		this.ownerUuid = ownerUuid;
		this.chunk = chunk;
		this.type = type;
		this.creationTime = creationTime;
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
}
