package net.dirtcraft.plugins.dirtloader.database.callbacks;

public interface ChunksCallback {
	void onQueryDone(int amount, String userName, String type);
}
