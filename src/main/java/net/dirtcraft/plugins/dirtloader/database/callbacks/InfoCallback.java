package net.dirtcraft.plugins.dirtloader.database.callbacks;

import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;

import java.util.List;

public interface InfoCallback {
	void onNoChunkloaderFound();
	void onChunkloaderFound(List<ChunkLoader> chunkLoader);
}
