package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import org.bukkit.command.CommandSender;

public class InfoCommand {
	public static boolean run(CommandSender sender, String[] args) {
		sender.sendMessage(ChunkManager.getLoadedChunks().toString());
		return true;
	}
}
