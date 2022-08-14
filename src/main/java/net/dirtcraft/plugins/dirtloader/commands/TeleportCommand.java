package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.database.callbacks.TeleportCallback;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportCommand {

	public static boolean run(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permissions.TELEPORT)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if (args.length != 2) {
			return true;
		}

		if (args[1].length() != 36) {
			return true;
		}

		teleportPlayer(sender, args[1]);

		return true;
	}

	private static void teleportPlayer(final CommandSender sender, final String chunkloaderUuid) {
		Player player = (Player) sender;
		DatabaseOperation.getChunkloader(UUID.fromString(chunkloaderUuid), player.getUniqueId(), sender.hasPermission(Permissions.TELEPORT_OTHER), new TeleportCallback() {

			@Override
			public void onNotChunkloaderOwner() {
				sender.sendMessage(Strings.NOT_CHUNKLOADER_OWNER);
			}

			@Override
			public void onSuccess(net.dirtcraft.plugins.dirtloader.data.Chunk chunk) {
				World world = Bukkit.getWorld(chunk.getWorld());
				Chunk chunkToTeleportTo = world.getChunkAt(chunk.getX(), chunk.getZ());
				int x = chunkToTeleportTo.getBlock(7, 0, 7).getX();
				int z = chunkToTeleportTo.getBlock(7, 0, 7).getZ();
				int y = world.getHighestBlockYAt(x, z) + 5;

				Location loc = new Location(world, x, y, z);
				player.teleport(loc);
				sender.sendMessage(Strings.TELEPORTED.replace("{X}", Integer.toString(chunk.getX())).replace("{Z}", Integer.toString(chunk.getZ())));
			}

			@Override
			public void onChunkloaderNotFound() {
				sender.sendMessage(Strings.CHUNKLOADER_NOT_FOUND);
			}
		});
	}
}
