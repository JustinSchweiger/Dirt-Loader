package net.dirtcraft.plugins.dirtloader.commands;

import net.dirtcraft.plugins.dirtloader.data.Chunk;
import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;
import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import net.dirtcraft.plugins.dirtloader.database.DatabaseOperation;
import net.dirtcraft.plugins.dirtloader.database.callbacks.LoadChunkCallback;
import net.dirtcraft.plugins.dirtloader.utils.Strings;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoadCommand {
	public static boolean run(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Strings.NO_CONSOLE);
			return false;
		}

		if (args.length != 2) {
			sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl load <online/offline>");
			return true;
		}

		if (!(args[1].equalsIgnoreCase("online") || args[1].equalsIgnoreCase("offline"))) {
			sender.sendMessage(Strings.INVALID_ARGUMENTS_USAGE + ChatColor.RED + "/dl load <online/offline>");
			return false;
		}

		if (args[1].equalsIgnoreCase("offline") && !Utilities.config.offlineLoader.enabled) {
			sender.sendMessage(Strings.OFFLINE_LOADER_DISABLED);
			return false;
		}

		String type = args[1].toLowerCase().trim();
		Player player = (Player) sender;
		UUID ownerUuid = player.getUniqueId();

		Chunk chunk = new Chunk(player.getLocation().getChunk().getWorld().getName(), player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());

		ChunkLoader chunkloader = new ChunkLoader(ownerUuid, chunk, type);

		addChunkloaderToDatabase(sender, ownerUuid, chunkloader);
		return true;
	}

	public static void addChunkloaderToDatabase(CommandSender sender, UUID ownerUuid, ChunkLoader chunkloader) {
		DatabaseOperation.addChunkloaderToPlayer(ownerUuid, chunkloader, new LoadChunkCallback() {
			@Override
			public void onNotEnoughAvailable(String type) {
				sender.sendMessage(Strings.NOT_ENOUGH_LOADERS.replace("{type}", type));
				Utilities.playErrorSound(sender);
			}

			@Override
			public void onSuccess(String type, int chunkX, int chunkZ) {
				ChunkManager.addChunk(ownerUuid, chunkloader);
				sender.sendMessage(Strings.CHUNK_LOADED.replace("{type}", type).replace("{X}", Integer.toString(chunkX)).replace("{Z}", Integer.toString(chunkZ)));
				Utilities.playSuccessSound(sender);
			}

			@Override
			public void onChunkAlreadyLoaded() {
				sender.sendMessage(Strings.CHUNK_ALREADY_LOADED);
				Utilities.playErrorSound(sender);
			}
		});
	}
}
