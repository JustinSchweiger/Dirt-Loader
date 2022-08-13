package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.database.callbacks.PlayerCallback;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class ListCommand {

	public static boolean run(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Strings.NO_CONSOLE);
			return false;
		}

		if (!(sender.hasPermission(Permissions.LIST) || sender.hasPermission(Permissions.LIST_OTHER))) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		Player player = (Player) sender;

		if (sender.hasPermission(Permissions.LIST) && !sender.hasPermission(Permissions.LIST_OTHER)) {
			// User has List Perms
			int page = 1;
			if (args.length == 2) {
				if (Utilities.isInteger(args[1])) {
					page = Integer.parseInt(args[1]);
				} else {
					sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl list [page]");
					return true;
				}
			}

			showList(sender, player.getName(), page);
		} else {
			// User has List other Perms
			if (args.length == 1) {
				showList(sender, player.getName(), 1);
				return true;
			} else if (args.length == 2) {
				if (Utilities.isInteger(args[1])) {
					int page = Integer.parseInt(args[1]);
					showList(sender, player.getName(), page);
				} else {
					showList(sender, args[1], 1);
				}
				return true;
			} else if (args.length == 3) {
				if (Utilities.isInteger(args[2])) {
					int page = Integer.parseInt(args[2]);
					showList(sender, args[1], page);
				} else {
					sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl list [user] [page]");
				}
				return true;
			} else {
				sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl list [user] [page]");
				return true;
			}
		}

		return true;
	}

	private static void showList(final CommandSender sender, final String playerForList, final int page) {
		DatabaseOperation.getPlayer("player_name", playerForList, new PlayerCallback() {
			@Override
			public void onPlayerFound(net.dirtcraft.plugins.dirtloader.data.Player player) {
				sendListToPlayer(sender, player, page);
			}

			@Override
			public void onPlayerNotFound(String value) {
				sender.sendMessage(Strings.PLAYER_NOT_FOUND + ChatColor.RED + value);
			}
		});
	}

	private static void sendListToPlayer(CommandSender sender, net.dirtcraft.plugins.dirtloader.data.Player player, int page) {
		if (player.getChunkLoaders().isEmpty()) {
			sender.sendMessage(Strings.NO_CHUNKS_LOADED);
			return;
		}

		int listEntries = Utilities.config.general.listEntries;

		int maxPages = (int) Math.ceil((double) player.getChunkLoaders().size() / (double) listEntries);
		if (page > maxPages) {
			sender.sendMessage(Strings.PAGE_INDEX_OUT_OF_BOUNDS + " Index must be smaller or equal to: " + maxPages);
			return;
		}

		int start = (page - 1) * listEntries;
		int end = page * listEntries;
		if (end > player.getChunkLoaders().size()) {
			end = player.getChunkLoaders().size();
		}

		sender.sendMessage(Strings.BAR_TOP);
		sender.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GRAY + "'s Chunks:");

		boolean senderEqualsPlayer = sender.getName().equals(player.getName());

		for (int i = start; i < end; i++) {
			TextComponent unload = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "\u2715" + ChatColor.DARK_GRAY + "]");
			unload.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl unload " + player.getUuid() + " " + player.getChunkLoaders().get(i).getUuid()));
			unload.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Strings.CLICK_TO_UNLOAD)));

			TextComponent teleport = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "\u2714 " + ChatColor.DARK_GREEN + "Teleport" + ChatColor.GREEN + " \u2714" + ChatColor.DARK_GRAY + "]");
			teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl teleport " + player.getChunkLoaders().get(i).getUuid()));
			teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Strings.CLICK_TO_TELEPORT)));




			if ((senderEqualsPlayer && permUnload) || (!senderEqualsPlayer && permUnloadOther)) {
				String[] chunkData = Utilities.getPlayerFile(player).getStringList("chunks").get(i).split("#");
				TextComponent entry = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "\u2715" + ChatColor.DARK_GRAY + "]");
				entry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl unload " + player.getName() + " " + Utilities.getPlayerFile(player).getStringList("chunks").get(i)));
				entry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY + "Unload chunk")));
				TextComponent chunkloaderPart = new TextComponent(ChatColor.GRAY + " - " + ChatColor.GOLD + "Chunkloader");
				if (!permTeleport) {
					chunkloaderPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
							ChatColor.GRAY + "Owner" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getName() + "\n" +
									"\n" +
									ChatColor.GRAY + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + chunkData[3] + "\n" +
									ChatColor.GRAY + "World" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + chunkData[0] + "\n" +
									ChatColor.GRAY + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Chunk " + ChatColor.DARK_AQUA + chunkData[1] + ChatColor.GOLD + " | " + ChatColor.DARK_AQUA + chunkData[2] + "\n" +
									ChatColor.GRAY + "Created" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + chunkData[4].split("_")[0] + ChatColor.GRAY + " at " + ChatColor.GOLD + chunkData[4].split("_")[1]
					)));
				} else {
					World world = Bukkit.getWorld(chunkData[0]);
					assert world != null;
					Chunk chunk = world.getChunkAt(Integer.parseInt(chunkData[1]), Integer.parseInt(chunkData[2]));
					int x = chunk.getBlock(7, 0, 7).getX();
					int z = chunk.getBlock(7, 0, 7).getZ();
					int y = world.getHighestBlockYAt(x, z) + 5;
					chunkloaderPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
							ChatColor.GRAY + "Owner" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getName() + "\n" +
									"\n" +
									ChatColor.GRAY + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + chunkData[3] + "\n" +
									ChatColor.GRAY + "World" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + chunkData[0] + "\n" +
									ChatColor.GRAY + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Chunk " + ChatColor.DARK_AQUA + chunkData[1] + ChatColor.GOLD + " | " + ChatColor.DARK_AQUA + chunkData[2] + "\n" +
									ChatColor.GRAY + "Created" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + chunkData[4].split("_")[0] + ChatColor.GRAY + " at " + ChatColor.GOLD + chunkData[4].split("_")[1] + "\n" +
									"\n" +
									ChatColor.DARK_AQUA + "Click to teleport to this chunk" + ChatColor.GRAY + "."
					)));
					chunkloaderPart.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Utilities.config.get("general.teleport-command") + " " + sender.getName() + " " + x + " " + y + " " + z));
				}
				entry.addExtra(chunkloaderPart);
				sender.spigot().sendMessage(entry);
			}
		}
		sender.sendMessage(Strings.BAR_BOTTOM);*/
	}
}
