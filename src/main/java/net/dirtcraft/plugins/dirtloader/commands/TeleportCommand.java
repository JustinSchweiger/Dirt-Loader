package net.dirtcraft.plugins.dirtloader.commands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class TeleportCommand {

	public static boolean run(CommandSender sender, String[] args) {
		World world = Bukkit.getWorld("world");
		Chunk chunk = world.getChunkAt(Integer.parseInt("x"), Integer.parseInt("z"));
		int x = chunk.getBlock(7, 0, 7).getX();
		int z = chunk.getBlock(7, 0, 7).getZ();
		int y = world.getHighestBlockYAt(x, z) + 5;

		return true;
	}
}
