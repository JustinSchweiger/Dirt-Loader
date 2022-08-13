package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChunksCommand {
	public static boolean run(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permissions.CHUNKS)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if (args.length != 5) {
			sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl chunks <add/remove> <user> <type> <amount>");
			return true;
		}

		String operation = args[1];
		String userName = args[2];
		String type = args[3];
		String amount = args[4];

		if (!((operation.equalsIgnoreCase("add") || operation.equalsIgnoreCase("remove") && (userName.length() <= 16 && userName.length() >= 3) && (type.equalsIgnoreCase("online") || type.equalsIgnoreCase("offline")) && Utilities.isInteger(amount)))) {
			sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl chunks <add/remove> <user> <type> <amount>");
			return true;
		}

		int amountInt = Integer.parseInt(amount);

		if (amountInt < 1) {
			sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl chunks <add/remove> <user> <type> <amount>");
			return true;
		}

		if (operation.equalsIgnoreCase("add")) {
			addChunks(sender, userName, type, amountInt);
		} else {
			removeChunks(sender, userName, type, amountInt);
		}

		return true;
	}

	private static void removeChunks(final CommandSender sender, final String userName, final String type, final int amountInt) {
		DatabaseOperation.removeChunksFromBalance(userName, type, amountInt, (amount, userName1, type1) -> sender.sendMessage(Strings.CHUNKS_REMOVED.replace("{amount}", String.valueOf(amount)).replace("{user}", userName1).replace("{type}", type1)));
	}

	private static void addChunks(final CommandSender sender, final String userName, final String type, final int amountInt) {
		DatabaseOperation.addChunksToBalance(userName, type, amountInt, (amount, userName1, type1) -> sender.sendMessage(Strings.CHUNKS_ADDED.replace("{amount}", String.valueOf(amount)).replace("{user}", userName1).replace("{type}", type1)));
	}
}
