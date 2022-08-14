package net.dirtcraft.plugins.dirtloader.database.callbacks;

import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;

import java.util.List;

public interface OfflineChunkloaderCallback {
	void onSuccess(List<ChunkLoader> chunkloaders);
}
