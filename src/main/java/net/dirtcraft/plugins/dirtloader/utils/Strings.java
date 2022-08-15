package net.dirtcraft.plugins.dirtloader.utils;

import org.bukkit.ChatColor;

public class Strings {
	// ---------------------------------------------------------- GENERAL ----------------------------------------------------------
	public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.RED + "DirtLoader" + ChatColor.GRAY + "] ";
	public static final String INTERNAL_PREFIX = ChatColor.GRAY + "[" + ChatColor.RED + "DirtLoader" + ChatColor.GRAY + "] ";
	public static final String BAR_TOP = ChatColor.DARK_RED + "---------------" + ChatColor.GRAY + "[ " + ChatColor.RED + "DirtCraft " + ChatColor.GOLD + "Chunkloading" + ChatColor.GRAY + " ]" + ChatColor.DARK_RED + "---------------\n";
	public static final String BAR_BOTTOM = ChatColor.DARK_RED + "----------------------------------------------------\n";
	public static final String HELP_INFO = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "info\n";
	public static final String HELP_LIST = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "list [page]\n";
	public static final String HELP_LIST_OTHER = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "list [user] [page]\n";
	public static final String HELP_BAL = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "bal\n";
	public static final String HELP_BAL_USER = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "bal [user]\n";
	public static final String HELP_CHUNKS = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "chunks <add/remove> <user> <type> <amount>\n";
	public static final String HELP_RELOAD = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "reload\n";
	public static final String HELP_LOAD = ChatColor.GOLD + "  /dl " + ChatColor.YELLOW + "load <online/offline>\n";
	public static final String NO_PERMISSION = PREFIX + ChatColor.RED + "You do not have permission to use this command.\n";
	public static final String INVALID_ARGUMENTS_USAGE = PREFIX + ChatColor.DARK_RED + "Invalid arguments. \nUsage: ";
	public static final String NO_CONSOLE = PREFIX + ChatColor.RED + "You must be a player to use this command.";
	public static final String PLAYER_NOT_FOUND = PREFIX + ChatColor.YELLOW + "No results found for player ";
	public static final String UNKNOWN_COMMAND = PREFIX + ChatColor.RED + "Unknown command ";
	public static final String INVALID_USERNAME_OR_UUID = PREFIX + ChatColor.RED + "Invalid username or UUID!";
	public static final String PURGE_OFFLINE_LOADER = "Purging offline loader at ({X} | {Z})";
	public static final String NO_PURGE_BECAUSE_ONLINE = "Chunk ({X} | {Z}) would have been purged but the player was online!";

	// ------------------------------------------------------- LOAD COMMAND -------------------------------------------------------
	public static final String CHUNK_ALREADY_LOADED = PREFIX + ChatColor.RED + "This chunk is already loaded!";
	public static final String NOT_ENOUGH_LOADERS = PREFIX + ChatColor.RED + "You do not have enough " + ChatColor.DARK_AQUA + "{type}" + ChatColor.RED + " loaders available!";
	public static final String CHUNK_LOADED = PREFIX + ChatColor.GREEN + "Chunk " + ChatColor.GRAY + "(" + ChatColor.DARK_AQUA + "{X} " + ChatColor.GRAY + "| " + ChatColor.DARK_AQUA + "{Z}" + ChatColor.GRAY + ") " + ChatColor.GREEN + "of type " + ChatColor.AQUA + "{type}" + ChatColor.GREEN + " has been successfully loaded!";
	public static final String OFFLINE_LOADER_DISABLED = "Offline loaders are disabled! Please activate them in the config!";

	// ------------------------------------------------------ UNLOAD COMMAND ------------------------------------------------------
	public static final String CHUNK_UNLOADED = PREFIX + ChatColor.GREEN + "Chunk " + ChatColor.GRAY + "(" + ChatColor.DARK_AQUA + "{X} " + ChatColor.GRAY + "| " + ChatColor.DARK_AQUA + "{Z}" + ChatColor.GRAY + ") " + ChatColor.GREEN + "of type " + ChatColor.AQUA + "{type}" + ChatColor.GREEN + " has been successfully unloaded!";

	// ------------------------------------------------------ CHUNKS COMMAND ------------------------------------------------------
	public static final String CHUNKS_ADDED = PREFIX + ChatColor.GRAY + "Added " + ChatColor.DARK_AQUA + "{amount} " + ChatColor.YELLOW + "{type} " + ChatColor.GRAY + "chunks to " + ChatColor.DARK_AQUA + "{user}" + ChatColor.GRAY + "!";
	public static final String CHUNKS_REMOVED = PREFIX + ChatColor.GRAY + "Removed " + ChatColor.DARK_AQUA + "{amount} " + ChatColor.YELLOW + "{type} " + ChatColor.GRAY + "chunks from " + ChatColor.DARK_AQUA + "{user}" + ChatColor.GRAY + "!";

	// ------------------------------------------------------- LIST COMMAND -------------------------------------------------------
	public static final String PAGE_INDEX_OUT_OF_BOUNDS = PREFIX + ChatColor.RED + "Page index out of bounds!";
	public static final String NO_CHUNKS_LOADED = PREFIX + ChatColor.RED + "No chunks loaded yet!\nUse " + ChatColor.GOLD + "/dl load <online/offline>" + ChatColor.RED + " to load chunks.";
	public static final String CLICK_TO_UNLOAD = ChatColor.UNDERLINE + "" + ChatColor.DARK_PURPLE + "Click me to unload this chunk!";
	public static final String CLICK_TO_TELEPORT = ChatColor.UNDERLINE + "" + ChatColor.DARK_PURPLE + "Click me to teleport to this chunk!";
	public static final String HALF_BAR_BOTTOM = ChatColor.DARK_RED + "--------------------";

	// ------------------------------------------------------ RELOAD COMMAND ------------------------------------------------------
	public static final String CONFIG_RELOADED = PREFIX + ChatColor.GREEN + "Config reloaded!";

	// ------------------------------------------------------- INFO COMMAND -------------------------------------------------------
	public static final String NO_LOADERS_FOUND_IN_CHUNK = PREFIX + ChatColor.DARK_RED + "No chunkloaders found in this chunk!";
	public static final String CHUNKLOADERS_FOUND_IN_CHUNK = ChatColor.GRAY + "Found " + ChatColor.GREEN + "{amount}" + ChatColor.GRAY + " chunkloaders in Chunk (" + ChatColor.DARK_AQUA + "{X} " + ChatColor.GRAY + "| " + ChatColor.DARK_AQUA + "{Z}" + ChatColor.GRAY + ")" + ChatColor.GRAY + ":";

	// ----------------------------------------------------- TELEPORT COMMAND -----------------------------------------------------
	public static final String NOT_CHUNKLOADER_OWNER = PREFIX + ChatColor.RED + "You are not the owner of this chunkloader!";
	public static final String CHUNKLOADER_NOT_FOUND = PREFIX + ChatColor.RED + "Chunkloader not found!";
	public static final String TELEPORTED = PREFIX + ChatColor.GREEN + "You have been teleported to Chunk " + ChatColor.GRAY + "(" + ChatColor.DARK_AQUA + "{X} " + ChatColor.GRAY + "| " + ChatColor.DARK_AQUA + "{Z}" + ChatColor.GRAY + ")" + ChatColor.GRAY + "!";
}
