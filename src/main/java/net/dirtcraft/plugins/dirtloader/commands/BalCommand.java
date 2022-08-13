package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.database.callbacks.PlayerCallback;
import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class BalCommand {
	public static boolean run(CommandSender sender, String[] args) {
		if (!(sender.hasPermission(Permissions.BAL) || sender.hasPermission(Permissions.BAL_OTHER))) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if (!(sender instanceof Player) && args.length != 2) {
			// console trying to run the command without player specified
			sender.sendMessage(Strings.NO_CONSOLE + " Please specify a player in the console!");
			return false;
		}

		if (args.length == 1) {
			Player player = (Player) sender;
			// Show balance for the player sending the command
			showBal(sender, "player_uuid", player.getUniqueId().toString());
			return true;
		}

		if (args.length == 2 && sender.hasPermission(Permissions.BAL_OTHER)) {
			String key;
			if (args[1].length() <= 16 && args[1].length() >= 3) {
				key = "player_name";
			} else if (args[1].length() == 36) {
				key = "player_uuid";
			} else {
				sender.sendMessage(Strings.INVALID_USERNAME_OR_UUID);
				return false;
			}

			// Show balance for the player specified
			showBal(sender, key, args[1]);
			return true;
		}

		return true;
	}

	private static void showBal(final CommandSender sender, final String key, final String value) {
		DatabaseOperation.getPlayer(key, value, new PlayerCallback() {
			@Override
			public void onPlayerFound(net.dirtcraft.plugins.dirtloader.data.Player player) {
				sender.sendMessage(Strings.BAR_TOP);
				sender.sendMessage(Utilities.format("&a" + player.getName() + "&7's Balance:\n"));
				sender.sendMessage(" \n");
				sender.sendMessage(Utilities.format("&3Online&6: &7" + player.getOnlineUsed() + " &6/ &7" + player.getOnlineAvailable()));
				sender.sendMessage(Utilities.format("&3Offline&6: &7" + player.getOfflineUsed() + " &6/ &7" + player.getOfflineAvailable()));
				sender.sendMessage(Strings.BAR_BOTTOM);
			}

			@Override
			public void onPlayerNotFound(String value) {
				if (sender instanceof Player) {
					sender.sendMessage(Strings.PLAYER_NOT_FOUND + ChatColor.RED + value);
				}
			}
		});
	}
}
