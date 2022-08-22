package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.database.callbacks.PlayerCallback;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import net.dirtcraft.plugins.dirtloader.utils.gradient.GradientHandler;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

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
		String key;

		if (playerForList.length() <= 16 && playerForList.length() >= 3) {
			key = "player_name";
		} else {
			key = "player_uuid";
		}

		DatabaseOperation.getPlayer(key, playerForList, new PlayerCallback() {
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

		boolean teleportPerm = sender.hasPermission(Permissions.TELEPORT);
		boolean teleportOtherPerm = sender.hasPermission(Permissions.TELEPORT_OTHER);
		boolean unloadPerm = sender.hasPermission(Permissions.UNLOAD);
		boolean unloadOtherPerm = sender.hasPermission(Permissions.UNLOAD_OTHER);
		boolean senderEqualsPlayer = sender.getName().equals(player.getName());

		sender.sendMessage(Strings.BAR_TOP);
		sender.sendMessage("");
		if (!senderEqualsPlayer) {
			sender.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GRAY + "'s Chunks:");
		}

		for (int i = start; i < end; i++) {
			BaseComponent[] unloadComponent = new ComponentBuilder("")
					.append(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "\u2715" + ChatColor.DARK_GRAY + "]")
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl unload " + player.getUuid() + " " + player.getChunkLoaders().get(i).getUuid()))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Strings.CLICK_TO_UNLOAD))).create();

			BaseComponent[] teleportComponent = new ComponentBuilder("")
					.append(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Teleport" + ChatColor.DARK_GRAY + "]")
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl teleport " + player.getChunkLoaders().get(i).getUuid()))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Strings.CLICK_TO_TELEPORT))).create();

			BaseComponent[] chunkloaderPart = new ComponentBuilder("")
					.append(ChatColor.GOLD + player.getChunkLoaders().get(i).getType().substring(0, 1).toUpperCase() + player.getChunkLoaders().get(i).getType().substring(1).trim() + " Chunkloader")
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
							ChatColor.GRAY + "Owner" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getName() + "\n" +
									"\n" +
									ChatColor.GRAY + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + player.getChunkLoaders().get(i).getType() + "\n" +
									ChatColor.GRAY + "World" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getChunkLoaders().get(i).getChunk().getWorld() + "\n" +
									ChatColor.GRAY + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Chunk " + ChatColor.GRAY + "(" + ChatColor.DARK_AQUA + player.getChunkLoaders().get(i).getChunk().getX() + ChatColor.GRAY + " | " + ChatColor.DARK_AQUA + player.getChunkLoaders().get(i).getChunk().getZ() + ChatColor.GRAY + ")\n" +
									ChatColor.GRAY + "Created" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getChunkLoaders().get(i).getCreationTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ChatColor.GRAY + " at " + ChatColor.GOLD + player.getChunkLoaders().get(i).getCreationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
					))).create();

			BaseComponent[] entry = null;

			if ((senderEqualsPlayer && unloadPerm) || unloadOtherPerm) {
				if (teleportPerm || teleportOtherPerm) {
					entry = new ComponentBuilder("")
							.append(unloadComponent)
							.append(ChatColor.GRAY + " - ")
							.event((HoverEvent) null)
							.event((ClickEvent) null)
							.append(teleportComponent)
							.append(ChatColor.GRAY + " - ")
							.event((HoverEvent) null)
							.event((ClickEvent) null)
							.append(chunkloaderPart)
							.create();
				} else {
					entry = new ComponentBuilder("")
							.append(unloadComponent)
							.append(ChatColor.GRAY + " - ")
							.event((HoverEvent) null)
							.event((ClickEvent) null)
							.append(chunkloaderPart)
							.create();
				}
			}

			sender.spigot().sendMessage(entry);
		}

		TextComponent bottomBar = new TextComponent(TextComponent.fromLegacyText(GradientHandler.hsvGradient("-----------------------", new java.awt.Color(251, 121, 0), new java.awt.Color(247, 0, 0), GradientHandler::linear, net.md_5.bungee.api.ChatColor.STRIKETHROUGH)));
		TextComponent pagePrev;
		if (page == 1) {
			pagePrev = new TextComponent(ChatColor.GRAY + "  \u25C0 ");
			pagePrev.setBold(true);
			pagePrev.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "You are already on the first page!")));
		} else {
			pagePrev = new TextComponent(ChatColor.GREEN + "  \u25C0 ");
			pagePrev.setBold(true);
			pagePrev.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Previous page")));
			if (sender.hasPermission(Permissions.LIST_OTHER)) {
				pagePrev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl list " + player.getName() + " " + (page - 1)));
			} else {
				pagePrev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl list " + (page - 1)));
			}
		}
		bottomBar.addExtra(pagePrev);
		TextComponent pageNext;
		if (page == maxPages) {
			pageNext = new TextComponent(ChatColor.GRAY + " \u25B6  ");
			pagePrev.setBold(true);
			pageNext.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + "You are already on the last page!")));
		} else {
			pageNext = new TextComponent(ChatColor.GREEN + " \u25B6  ");
			pagePrev.setBold(true);
			pageNext.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GREEN + "Next page")));
			if (sender.hasPermission(Permissions.LIST_OTHER)) {
				pageNext.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl list " + player.getName() + " " + (page + 1)));
			} else {
				pageNext.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl list " + (page + 1)));
			}
		}
		bottomBar.addExtra(pageNext);
		bottomBar.addExtra(new TextComponent(TextComponent.fromLegacyText(GradientHandler.hsvGradient("-----------------------", new java.awt.Color(247, 0, 0), new java.awt.Color(251, 121, 0), GradientHandler::linear, net.md_5.bungee.api.ChatColor.STRIKETHROUGH))));
		sender.sendMessage("");
		sender.spigot().sendMessage(bottomBar);
	}
}
