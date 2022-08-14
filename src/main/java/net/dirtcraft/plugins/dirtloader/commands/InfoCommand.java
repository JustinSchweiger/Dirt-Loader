package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.data.Chunk;
import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;
import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.database.callbacks.InfoCallback;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InfoCommand {
	public static boolean run(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Strings.NO_CONSOLE);
			return false;
		}

		if (!sender.hasPermission(Permissions.INFO)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		Chunk chunk = new Chunk(
				((Player) sender).getWorld().getName().trim(),
				((Player) sender).getLocation().getChunk().getX(),
				((Player) sender).getLocation().getChunk().getZ()
		);

		showInfo(sender, chunk);

		return true;
	}

	private static void showInfo(final CommandSender sender, final Chunk chunk) {
		DatabaseOperation.findChunkloadersFromChunk(chunk, new InfoCallback() {

			@Override
			public void onNoChunkloaderFound() {
				sender.sendMessage(Strings.NO_LOADERS_FOUND_IN_CHUNK);
			}

			@Override
			public void onChunkloaderFound(List<ChunkLoader> chunkLoader) {
				Player player = (Player) sender;
				sender.sendMessage(Strings.BAR_TOP);
				sender.sendMessage(Strings.CHUNKLOADERS_FOUND_IN_CHUNK.replace("{X}", Integer.toString(player.getLocation().getChunk().getX())).replace("{Z}", Integer.toString(player.getLocation().getChunk().getZ())));
				for (ChunkLoader loader : chunkLoader) {
					BaseComponent[] unloadComponent = new ComponentBuilder("")
							.append(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "\u2715" + ChatColor.DARK_GRAY + "]")
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl unload " + loader.getOwnerUuid() + " " + loader.getUuid()))
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Strings.CLICK_TO_UNLOAD))).create();

					BaseComponent[] chunkloaderPart = new ComponentBuilder("")
							.append(ChatColor.GOLD + loader.getType().substring(0, 1).toUpperCase() + loader.getType().substring(1).trim() + " Chunkloader")
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
									ChatColor.GRAY + "Owner" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + player.getName() + "\n" +
											"\n" +
											ChatColor.GRAY + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + loader.getType() + "\n" +
											ChatColor.GRAY + "World" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + loader.getChunk().getWorld() + "\n" +
											ChatColor.GRAY + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Chunk " + ChatColor.GRAY + "(" + ChatColor.DARK_AQUA + loader.getChunk().getX() + ChatColor.GRAY + " | " + ChatColor.DARK_AQUA + loader.getChunk().getZ() + ChatColor.GRAY + ")\n" +
											ChatColor.GRAY + "Created" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + loader.getCreationTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ChatColor.GRAY + " at " + ChatColor.GOLD + loader.getCreationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
							))).create();

					BaseComponent[] entry;
					if (sender.hasPermission(Permissions.UNLOAD_OTHER)) {
						entry = new ComponentBuilder("")
								.append(unloadComponent)
								.append(ChatColor.GRAY + " - ")
								.event((HoverEvent) null)
								.event((ClickEvent) null)
								.append(chunkloaderPart)
								.create();
					} else {
						entry = new ComponentBuilder("")
								.append(chunkloaderPart)
								.create();
					}

					sender.spigot().sendMessage(entry);
				}
				sender.sendMessage(Strings.BAR_BOTTOM);
			}
		});
	}
}
