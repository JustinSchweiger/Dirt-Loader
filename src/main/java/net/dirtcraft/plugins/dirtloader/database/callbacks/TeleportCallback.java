package net.dirtcraft.plugins.dirtloader.database.callbacks;

import net.dirtcraft.plugins.dirtloader.data.Chunk;

public interface TeleportCallback {
	void onNotChunkloaderOwner();
	void onSuccess(Chunk chunk);
	void onChunkloaderNotFound();
}
