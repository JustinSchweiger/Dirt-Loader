package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
	public static boolean run(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permissions.RELOAD)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		Utilities.loadConfig();
		ChunkManager.restartOfflinePurgeTask();
		sender.sendMessage(Strings.CONFIG_RELOADED);
		Utilities.playSuccessSound(sender);
		return true;
	}
}
