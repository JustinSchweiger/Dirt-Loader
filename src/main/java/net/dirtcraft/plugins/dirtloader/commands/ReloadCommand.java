package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
	public static boolean run(CommandSender sender, String[] args) {
		Utilities.loadConfig();
		sender.sendMessage(Strings.CONFIG_RELOADED);
		Utilities.playSuccessSound(sender);
		return true;
	}
}
