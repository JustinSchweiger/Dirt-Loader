package net.dirtcraft.plugins.dirtloader;

import org.bukkit.ChatColor;

public class Strings {
	public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.RED + "DirtLoader" + ChatColor.GRAY + "] ";
	public static final String BAR_TOP = ChatColor.DARK_RED + "---------------" + ChatColor.GRAY + "[ " + ChatColor.RED + "DirtCraft " + ChatColor.GOLD + "Chunkloading" + ChatColor.GRAY + " ]" + ChatColor.DARK_RED + "---------------\n";
	public static final String BAR_BOTTOM = ChatColor.DARK_RED + "----------------------------------------------------\n";
	public static final String HELP_INFO = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "info\n";
	public static final String HELP_LIST = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "list [page]\n";
	public static final String HELP_LIST_USER = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "list [user] [page]\n";
	public static final String HELP_BAL = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "bal\n";
	public static final String HELP_BAL_USER = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "bal [user]\n";
	public static final String HELP_CHUNKS = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "chunks <add/remove> <user> <type> <amount>\n";
	public static final String HELP_RELOAD = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "reload\n";
	public static final String LOAD = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "load <online/offline>\n";
	public static final String NO_PERMISSION = PREFIX + ChatColor.RED + "You do not have permission to use this command.\n";
	public static final String INVALID_ARGUMENTS_USAGE = PREFIX + ChatColor.DARK_RED + "Invalid arguments. \nUsage: ";
	public static final String NO_CONSOLE = PREFIX + ChatColor.RED + "You must be a player to use this command.";
	public static final String ALREADY_LOADED = PREFIX + ChatColor.RED + "This chunk is already loaded!";
	public static final String NOT_ENOUGH_LOADERS = PREFIX + ChatColor.DARK_RED + "Not enough loaders available!";
	public static final String PLAYER_NOT_FOUND = PREFIX + ChatColor.RED + "Player not found! Are they online?";
	public static final String CANT_REMOVE_MORE_CHUNKS = PREFIX + ChatColor.RED + "You can't remove any more chunks!";
	public static final String PAGE_INDEX_OUT_OF_BOUNDS = PREFIX + ChatColor.RED + "Page index out of bounds!";
	public static final String NO_CHUNKS_LOADED = PREFIX + ChatColor.RED + "No chunks loaded yet!\nUse " + ChatColor.GOLD + "/dl load <online/offline>" + ChatColor.RED + " to load chunks.";
	public static final String NO_UNLOAD_OTHER_PERMS = PREFIX + ChatColor.RED + "You do not have permission to unload other players' chunks!";
	public static final String CONFIG_RELOADED = PREFIX + ChatColor.GREEN + "Config reloaded!";
	public static final String NO_LOADERS_FOUND_IN_CHUNK = PREFIX + ChatColor.RED + "No chunkloaders found in chunk!";
}
