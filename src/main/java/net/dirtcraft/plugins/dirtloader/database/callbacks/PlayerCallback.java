package net.dirtcraft.plugins.dirtloader.database.callbacks;

import net.dirtcraft.plugins.dirtloader.data.Player;

public interface PlayerCallback {
	void onPlayerFound(Player player);
	void onPlayerNotFound(String value);
}
