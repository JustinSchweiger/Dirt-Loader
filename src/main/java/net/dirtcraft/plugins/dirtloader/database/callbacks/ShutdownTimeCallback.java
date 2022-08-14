package net.dirtcraft.plugins.dirtloader.database.callbacks;

import java.time.LocalDateTime;

public interface ShutdownTimeCallback {
	void onSuccess(LocalDateTime shutdownTime);
}
