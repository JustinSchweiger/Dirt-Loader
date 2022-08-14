package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class BaseCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission(Permissions.BASE)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
			ArrayList<String> listings = getListings(sender);
			StringBuilder message = new StringBuilder();
			for (String listing : listings) {
				message.append(listing);
			}
			sender.sendMessage(Strings.BAR_TOP + message + Strings.BAR_BOTTOM);
			return true;
		}

		System.out.println(ChunkManager.getLoadedChunks().toString());

		String arg = args[0].toLowerCase();

		switch (arg) {
			case "list":
				return ListCommand.run(sender, args);
			case "bal":
				return BalCommand.run(sender, args);
			case "chunks":
				return ChunksCommand.run(sender, args);
			case "reload":
				return ReloadCommand.run(sender, args);
			case "info":
				return InfoCommand.run(sender, args);
			case "load":
				return LoadCommand.run(sender, args);
			case "unload":
				return UnloadCommand.run(sender, args);
			case "teleport":
				return TeleportCommand.run(sender, args);
			default:
				sender.sendMessage(Strings.UNKNOWN_COMMAND + " " + ChatColor.DARK_RED + arg);
		}


		if (args[0].equalsIgnoreCase("info") && sender.hasPermission(Permissions.INFO)) {
			if (args.length != 1) {
				sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl info");
				return false;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(Strings.NO_CONSOLE);
				return false;
			}

			Player player = (Player) sender;
			Chunk currentChunk = player.getLocation().getChunk();
			int chunkX = currentChunk.getX();
			int chunkZ = currentChunk.getZ();
			String world = currentChunk.getWorld().getName();
			String chunkString = world + "#" + chunkX + "#" + chunkZ;

			showInfo(sender, chunkString);
		}

		return false;
	}

	private void showInfo(CommandSender sender, String chunkString) {
		/*File[] fileList = Utilities.getAllPlayerdataFiles();
		HashMap<String, String> loadersFound = new HashMap<>();

		if (fileList == null) {
			sender.sendMessage(ChatColor.RED + "No playerdata found!");
			return;
		}

		for (File file : fileList) {
			if (file.isFile()) {
				FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
				for (String chunk : playerFile.getStringList("chunks")) {
					if (chunk.contains(chunkString)) {
						loadersFound.put(playerFile.getString("name"), chunk);
					}
				}
			}
		}

		if (loadersFound.isEmpty()) {
			sender.sendMessage(Strings.NO_LOADERS_FOUND_IN_CHUNK);
			return;
		}

		sender.sendMessage(Strings.BAR_TOP);
		sender.sendMessage(ChatColor.GRAY + "Chunk ( " + ChatColor.DARK_AQUA + chunkString.split("#")[1] + ChatColor.GOLD + " | " + ChatColor.DARK_AQUA + chunkString.split("#")[2] + ChatColor.GRAY + " ) is loaded by:");


		for (Map.Entry<String, String> entry : loadersFound.entrySet()) {
			TextComponent foundLoader = null;
			if (sender.hasPermission(Permissions.UNLOAD_OTHER)) {
				foundLoader = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "\u2715" + ChatColor.DARK_GRAY + "]");
				foundLoader.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dl unload " + entry.getKey() + " " + entry.getValue()));
				foundLoader.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY + "Unload chunk")));
			}

			TextComponent chunkloaderPart = new TextComponent(ChatColor.GRAY + " - " + ChatColor.GOLD + entry.getKey());
			chunkloaderPart.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
					ChatColor.GRAY + "Owner" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + entry.getKey() + "\n" +
							"\n" +
							ChatColor.GRAY + "Type" + ChatColor.DARK_GRAY + ": " + ChatColor.AQUA + entry.getValue().split("#")[3] + "\n" +
							ChatColor.GRAY + "World" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + entry.getValue().split("#")[0] + "\n" +
							ChatColor.GRAY + "Location" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + "Chunk " + ChatColor.DARK_AQUA + entry.getValue().split("#")[1] + ChatColor.GOLD + " | " + ChatColor.DARK_AQUA + entry.getValue().split("#")[2] + "\n" +
							ChatColor.GRAY + "Created" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + entry.getValue().split("#")[4].split("_")[0] + ChatColor.GRAY + " at " + ChatColor.GOLD + entry.getValue().split("#")[4].split("_")[1]
			)));
			chunkloaderPart.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ""));

			if (foundLoader == null) {
				sender.spigot().sendMessage(chunkloaderPart);
			} else {
				foundLoader.addExtra(chunkloaderPart);
				sender.spigot().sendMessage(foundLoader);
			}

			sender.sendMessage(Strings.BAR_BOTTOM);
		}*/
	}

	private ArrayList<String> getListings(CommandSender sender) {
		ArrayList<String> listings = new ArrayList<>();
		if (sender.hasPermission(Permissions.LIST_OTHER)) {
			listings.add(Strings.HELP_LIST_USER);
		}

		if (sender.hasPermission(Permissions.LIST) && !sender.hasPermission(Permissions.LIST_OTHER)) {
			listings.add(Strings.HELP_LIST);
		}

		if (sender.hasPermission(Permissions.BAL_OTHER)) {
			listings.add(Strings.HELP_BAL_USER);
		}

		if (sender.hasPermission(Permissions.BAL) && !sender.hasPermission(Permissions.BAL_OTHER)) {
			listings.add(Strings.HELP_BAL);
		}

		if (sender.hasPermission(Permissions.CHUNKS)) {
			listings.add(Strings.HELP_CHUNKS);
		}

		if (sender.hasPermission(Permissions.RELOAD)) {
			listings.add(Strings.HELP_RELOAD);
		}

		if (sender.hasPermission(Permissions.INFO)) {
			listings.add(Strings.HELP_INFO);
		}

		if (sender.hasPermission(Permissions.LOAD)) {
			listings.add(Strings.HELP_LOAD);
		}

		return listings;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> arguments = new ArrayList<>();

		if (args.length == 1) {
			if (sender.hasPermission(Permissions.LIST_OTHER)) {
				arguments.add("list");
			}

			if (sender.hasPermission(Permissions.LOAD)) {
				arguments.add("help");
			}

			if (sender.hasPermission(Permissions.BAL_OTHER) || sender.hasPermission(Permissions.BAL)) {
				arguments.add("bal");
			}

			if (sender.hasPermission(Permissions.CHUNKS)) {
				arguments.add("chunks");
			}

			if (sender.hasPermission(Permissions.RELOAD)) {
				arguments.add("reload");
			}

			if (sender.hasPermission(Permissions.INFO)) {
				arguments.add("info");
			}

			if (sender.hasPermission(Permissions.LOAD)) {
				arguments.add("load");
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("list") && sender.hasPermission(Permissions.LIST_OTHER)) {
			for (Player player : sender.getServer().getOnlinePlayers()) {
				arguments.add(player.getName());
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("bal") && sender.hasPermission(Permissions.BAL_OTHER)) {
			for (Player player : sender.getServer().getOnlinePlayers()) {
				arguments.add(player.getName());
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("load") && sender.hasPermission(Permissions.LOAD)) {
			arguments.add("online");
			arguments.add("offline");
		} else if (args.length == 2 && args[0].equalsIgnoreCase("chunks") && sender.hasPermission(Permissions.CHUNKS)) {
			arguments.add("add");
			arguments.add("remove");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("chunks") && sender.hasPermission(Permissions.CHUNKS)) {
			for (Player player : sender.getServer().getOnlinePlayers()) {
				arguments.add(player.getName());
			}
		} else if (args.length == 4 && args[0].equalsIgnoreCase("chunks") && sender.hasPermission(Permissions.CHUNKS)) {
			arguments.add("online");
			arguments.add("offline");
		}

		List<String> tabResults = new ArrayList<>();
		for (String argument : arguments) {
			if (argument.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
				tabResults.add(argument);
			}
		}

		return tabResults;
	}
}
