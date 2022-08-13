package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.DirtLoader;
import net.dirtcraft.plugins.dirtloader.data.Chunk;
import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;
import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.database.callbacks.UnloadChunkCallback;
import net.dirtcraft.plugins.dirtloader.utils.Permissions;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

public class UnloadCommand {
	public static boolean run(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Strings.NO_CONSOLE);
			return false;
		}

		if (!(sender.hasPermission(Permissions.UNLOAD) || sender.hasPermission(Permissions.UNLOAD_OTHER))) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		if (args.length != 3) {
			if (Utilities.config.general.debug) {
				Utilities.log(Level.SEVERE, "Invalid arguments for unload: " + Arrays.toString(args));
			}
			return true;
		}

		Player player = (Player) sender;

		if (!args[1].equals(player.getUniqueId().toString()) || !sender.hasPermission(Permissions.UNLOAD_OTHER)) {
			sender.sendMessage(Strings.NO_PERMISSION);
			return true;
		}

		unloadChunk(sender, UUID.fromString(args[1]), UUID.fromString(args[2]));

		return true;
	}

	private static void unloadChunk(final CommandSender sender, final UUID playerUuid, final UUID chunkloaderUuid) {
		DatabaseOperation.removeChunkloaderFromPlayer(playerUuid, chunkloaderUuid, chunkLoader -> {
			ChunkManager.removeChunk(playerUuid, chunkLoader);
			sender.sendMessage(Strings.CHUNK_UNLOADED.replace("{X}", Integer.toString(chunkLoader.getChunk().getX())).replace("{Z}", Integer.toString(chunkLoader.getChunk().getZ())).replace("{TYPE}", chunkLoader.getType()));
			Utilities.playUnloadSound(sender);
		});
	}
}
