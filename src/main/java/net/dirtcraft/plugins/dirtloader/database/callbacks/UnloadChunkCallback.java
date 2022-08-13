package net.dirtcraft.plugins.dirtloader.database.callbacks;

import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;

public interface UnloadChunkCallback {
	void onSuccess(ChunkLoader chunkLoader);
}
