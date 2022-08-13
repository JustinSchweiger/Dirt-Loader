package net.dirtcraft.plugins.dirtloader.database.callbacks;

public interface LoadChunkCallback {
	void onNotEnoughAvailable(String type);
	void onSuccess(String type, int chunkX, int chunkZ);
	void onChunkAlreadyLoaded();
}
