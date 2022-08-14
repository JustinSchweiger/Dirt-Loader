package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;
import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.ChatColor;
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

		String arg = args[0].toLowerCase();
		/*
		                      !!!!!!!!!!ACTIVATE FOR OUTPUT OF LOADED CHUNKS VARIABLE AFTER EVERY COMMAND!!!!!!!!!!!!!

		HashMap<UUID, List<ChunkLoader>> chunkloaders = ChunkManager.getLoadedChunks();
		for (Map.Entry<UUID, List<ChunkLoader>> chunkloader : chunkloaders.entrySet()) {
			for (ChunkLoader loader : chunkloader.getValue()) {
				System.out.println("UUID: " + loader.getOwnerUuid() + "\t\t" + loader.getChunk().getX() + " | " + loader.getChunk().getZ());
			}
		}
		*/

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

		return true;
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
