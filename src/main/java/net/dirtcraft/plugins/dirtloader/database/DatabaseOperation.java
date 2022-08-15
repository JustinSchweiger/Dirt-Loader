package net.dirtcraft.plugins.dirtloader.database;

import net.dirtcraft.plugins.dirtloader.DirtLoader;
import net.dirtcraft.plugins.dirtloader.data.Chunk;
import net.dirtcraft.plugins.dirtloader.data.ChunkLoader;
import net.dirtcraft.plugins.dirtloader.data.ChunkManager;
import net.dirtcraft.plugins.dirtloader.data.Player;
import net.dirtcraft.plugins.dirtloader.database.callbacks.*;
import net.dirtcraft.plugins.dirtloader.utils.Utilities;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseOperation {

	public static void getPlayer(final String key, final String value, final PlayerCallback callback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement findWithName = connection.prepareStatement("SELECT * FROM PLAYER WHERE PLAYER_NAME = ?");
			     PreparedStatement findWithUUID = connection.prepareStatement("SELECT * FROM PLAYER WHERE PLAYER_UUID = ?");
			     PreparedStatement getChunkloaders = connection.prepareStatement("SELECT * FROM LOADER WHERE PLAYER_OWNERUUID = ?")
			) {
				findWithName.setString(1, value);
				findWithUUID.setString(1, value);
				ResultSet playerResult;
				if (key.equalsIgnoreCase("player_name")) {
					playerResult = findWithName.executeQuery();
				} else {
					playerResult = findWithUUID.executeQuery();
				}

				if (!playerResult.next()) {
					Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> callback.onPlayerNotFound(value));
					return;
				}

				getChunkloaders.setString(1, playerResult.getString("player_uuid"));
				ResultSet chunkloaderResult = getChunkloaders.executeQuery();
				List<ChunkLoader> chunkloadersFound = new ArrayList<>();
				while (chunkloaderResult.next()) {
					LocalDateTime creationTime = LocalDateTime.parse(chunkloaderResult.getString("loader_creationtime"), DateTimeFormatter.ISO_DATE_TIME);
					LocalDateTime shutdownTime = LocalDateTime.parse(chunkloaderResult.getString("loader_shutdowntime"), DateTimeFormatter.ISO_DATE_TIME);
					chunkloadersFound.add(new ChunkLoader(
							UUID.fromString(chunkloaderResult.getString("loader_uuid")),
							UUID.fromString(chunkloaderResult.getString("player_owneruuid")),
							new Chunk(
									chunkloaderResult.getString("loader_world").trim(),
									chunkloaderResult.getInt("loader_x"),
									chunkloaderResult.getInt("loader_z")
							),
							chunkloaderResult.getString("loader_type").trim(),
							creationTime,
							shutdownTime
					));
				}

				final Player player = new Player(
						UUID.fromString(playerResult.getString("player_uuid")),
						playerResult.getString("player_name"),
						playerResult.getInt("player_onlineavailable"),
						playerResult.getInt("player_offlineavailable"),
						playerResult.getInt("player_onlineused"),
						playerResult.getInt("player_offlineused"),
						chunkloadersFound
				);

				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> callback.onPlayerFound(player));
			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not execute query!");
					e.printStackTrace();
				}
			}
		});
	}

	public static void insertPlayer(final Player player) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement statement = connection.prepareStatement("INSERT INTO PLAYER VALUES (?, ?, ?, ?, ?, ?)")
			) {
				statement.setString(1, player.getUuid().toString());
				statement.setString(2, player.getName());
				statement.setInt(3, player.getOnlineAvailable());
				statement.setInt(4, player.getOfflineAvailable());
				statement.setInt(5, player.getOnlineUsed());
				statement.setInt(6, player.getOfflineUsed());

				statement.executeUpdate();
			} catch (SQLException ignored) {}
		});
	}

	public static void removeChunksFromBalance(final String userName, final String type, final int amountInt, final ChunksCallback chunksCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement removeOnline = connection.prepareStatement("UPDATE PLAYER SET PLAYER_ONLINEAVAILABLE = GREATEST(0, PLAYER_ONLINEAVAILABLE - ?) WHERE PLAYER_NAME = ?");
			     PreparedStatement removeOffline = connection.prepareStatement("UPDATE PLAYER SET PLAYER_OFFLINEAVAILABLE = GREATEST(0, PLAYER_OFFLINEAVAILABLE - ?) WHERE PLAYER_NAME = ?")
			) {
				removeOnline.setInt(1, amountInt);
				removeOnline.setString(2, userName);
				removeOffline.setInt(1, amountInt);
				removeOffline.setString(2, userName);

				if (type.equalsIgnoreCase("online")) {
					removeOnline.executeUpdate();
				} else {
					removeOffline.executeUpdate();
				}

				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> {
					chunksCallback.onQueryDone(amountInt, userName, type);
				});

			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not execute query!");
					e.printStackTrace();
				}
			}
		});
	}

	public static void addChunksToBalance(final String userName, final String type, final int amountInt, final ChunksCallback chunksCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement addOnline = connection.prepareStatement("UPDATE PLAYER SET PLAYER_ONLINEAVAILABLE = PLAYER_ONLINEAVAILABLE + ? WHERE PLAYER_NAME = ?");
			     PreparedStatement addOffline = connection.prepareStatement("UPDATE PLAYER SET PLAYER_OFFLINEAVAILABLE = PLAYER_OFFLINEAVAILABLE + ? WHERE PLAYER_NAME = ?")
			) {
				addOnline.setInt(1, amountInt);
				addOnline.setString(2, userName);
				addOffline.setInt(1, amountInt);
				addOffline.setString(2, userName);

				if (type.equalsIgnoreCase("online")) {
					addOnline.executeUpdate();
				} else {
					addOffline.executeUpdate();
				}

				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> {
					chunksCallback.onQueryDone(amountInt, userName, type);
				});

			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not execute query!");
					e.printStackTrace();
				}
			}
		});
	}

	public static void addChunkloaderToPlayer(final UUID ownerUuid, final ChunkLoader chunkloader, final LoadChunkCallback callback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			if (ChunkManager.isChunkLoaded(ownerUuid, chunkloader.getChunk())) {
				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), callback::onChunkAlreadyLoaded);
				return;
			}

			String type = chunkloader.getType();
			int onlineLeft = 0;
			int offlineLeft = 0;

			try (Connection connection = Database.getConnection();
			     PreparedStatement statement = connection.prepareStatement("SELECT PLAYER_ONLINEAVAILABLE, PLAYER_OFFLINEAVAILABLE, PLAYER_ONLINEUSED, PLAYER_OFFLINEUSED FROM PLAYER WHERE PLAYER_UUID = ?")
			) {
				statement.setString(1, ownerUuid.toString());
				ResultSet resultSet = statement.executeQuery();

				if (resultSet.next()) {
					onlineLeft = resultSet.getInt("PLAYER_ONLINEAVAILABLE") - resultSet.getInt("PLAYER_ONLINEUSED");
					offlineLeft = resultSet.getInt("PLAYER_OFFLINEAVAILABLE") - resultSet.getInt("PLAYER_OFFLINEUSED");
				}
			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not execute query!");
					e.printStackTrace();
					return;
				}
			}

			if (type.equalsIgnoreCase("online") && onlineLeft < 1) {
				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> callback.onNotEnoughAvailable(type));
				return;
			} else if (type.equalsIgnoreCase("offline") && offlineLeft < 1) {
				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> callback.onNotEnoughAvailable(type));
				return;
			}

			try (Connection connection = Database.getConnection();
			     PreparedStatement insertLoader = connection.prepareStatement("INSERT INTO LOADER VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			     PreparedStatement updatePlayerOnline = connection.prepareStatement("UPDATE PLAYER SET PLAYER_ONLINEUSED = PLAYER_ONLINEUSED + 1 WHERE PLAYER_UUID = ?");
			     PreparedStatement updatePlayerOffline = connection.prepareStatement("UPDATE PLAYER SET PLAYER_OFFLINEUSED = PLAYER_OFFLINEUSED + 1 WHERE PLAYER_UUID = ?")
			) {
				LocalDateTime shutdownTime = LocalDateTime.now().plusHours(Utilities.config.offlineLoader.offlineLoaderDuration);
				insertLoader.setString(1, chunkloader.getUuid().toString());
				insertLoader.setString(2, ownerUuid.toString());
				insertLoader.setString(3, type);
				insertLoader.setString(4, chunkloader.getChunk().getWorld());
				insertLoader.setInt(5, chunkloader.getChunk().getX());
				insertLoader.setInt(6, chunkloader.getChunk().getZ());
				insertLoader.setString(7, chunkloader.getCreationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				insertLoader.setString(8, shutdownTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				updatePlayerOnline.setString(1, ownerUuid.toString());
				updatePlayerOffline.setString(1, ownerUuid.toString());
				insertLoader.executeUpdate();

				if (type.equalsIgnoreCase("online")) {
					updatePlayerOnline.executeUpdate();
				} else {
					updatePlayerOffline.executeUpdate();
				}
			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not insert new chunkloader and update the player!");
					e.printStackTrace();
					return;
				}
			}

			Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> callback.onSuccess(type, chunkloader.getChunk().getX(), chunkloader.getChunk().getZ()));
		});
	}

	public static void removeChunkloaderFromPlayer(final UUID playerUuid, final UUID chunkloaderUuid, final UnloadChunkCallback unloadChunkCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement getChunkloader = connection.prepareStatement("SELECT * FROM LOADER WHERE LOADER_UUID = ?");
			     PreparedStatement deleteLoader = connection.prepareStatement("DELETE FROM LOADER WHERE LOADER_UUID = ?");
			     PreparedStatement updatePlayerOnline = connection.prepareStatement("UPDATE PLAYER SET PLAYER_ONLINEUSED = PLAYER_ONLINEUSED - 1 WHERE PLAYER_UUID = ?");
			     PreparedStatement updatePlayerOffline = connection.prepareStatement("UPDATE PLAYER SET PLAYER_OFFLINEUSED = PLAYER_OFFLINEUSED - 1 WHERE PLAYER_UUID = ?")
			) {
				getChunkloader.setString(1, chunkloaderUuid.toString());
				ResultSet resultSet = getChunkloader.executeQuery();

				if (!resultSet.next()) {
					return;
				}

				Chunk chunk = new Chunk(
						resultSet.getString("LOADER_WORLD").trim(),
						resultSet.getInt("LOADER_X"),
						resultSet.getInt("LOADER_Z")
				);
				LocalDateTime creationTime = LocalDateTime.parse(resultSet.getString("LOADER_CREATIONTIME"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				LocalDateTime shutdownTime = LocalDateTime.parse(resultSet.getString("LOADER_SHUTDOWNTIME"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				ChunkLoader chunkloader = new ChunkLoader(
						chunkloaderUuid,
						playerUuid,
						chunk,
						resultSet.getString("LOADER_TYPE").trim(),
						creationTime,
						shutdownTime
				);

				String type = resultSet.getString("LOADER_TYPE").trim();

				deleteLoader.setString(1, chunkloaderUuid.toString());
				updatePlayerOnline.setString(1, playerUuid.toString());
				updatePlayerOffline.setString(1, playerUuid.toString());

				deleteLoader.executeUpdate();

				if (type.equalsIgnoreCase("online")) {
					updatePlayerOnline.executeUpdate();
				} else {
					updatePlayerOffline.executeUpdate();
				}

				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> unloadChunkCallback.onSuccess(chunkloader));
			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not execute query!");
					e.printStackTrace();
				}
			}
		});
	}

	public static void getChunkloader(final UUID chunkloaderUuid, final UUID playerUuid, final boolean hasTeleportOtherPerm, final TeleportCallback teleportCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement getChunkloader = connection.prepareStatement("SELECT * FROM LOADER WHERE LOADER_UUID = ?")
			) {
				getChunkloader.setString(1, chunkloaderUuid.toString());
				ResultSet resultSet = getChunkloader.executeQuery();

				if (!resultSet.next()) {
					Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), teleportCallback::onChunkloaderNotFound);
					return;
				}

				if (!UUID.fromString(resultSet.getString("PLAYER_OWNERUUID")).equals(playerUuid)) {
					if (!hasTeleportOtherPerm) {
						Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), teleportCallback::onNotChunkloaderOwner);
						return;
					}
				}

				Chunk chunk = new Chunk(
						resultSet.getString("LOADER_WORLD").trim(),
						resultSet.getInt("LOADER_X"),
						resultSet.getInt("LOADER_Z")
				);
				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> teleportCallback.onSuccess(chunk));
			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not execute query!");
					e.printStackTrace();
				}
			}
		});
	}

	public static void findChunkloadersFromChunk(final Chunk chunk, final InfoCallback infoCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement statement = connection.prepareStatement("SELECT * FROM LOADER WHERE LOADER_WORLD = ? AND LOADER_X = ? AND LOADER_Z = ?")
			) {
				statement.setString(1, chunk.getWorld());
				statement.setInt(2, chunk.getX());
				statement.setInt(3, chunk.getZ());
				ResultSet resultSet = statement.executeQuery();

				List<ChunkLoader> chunkloaders = new ArrayList<>();
				while (resultSet.next()) {
					chunkloaders.add(
							new ChunkLoader(
									UUID.fromString(resultSet.getString("LOADER_UUID")),
									UUID.fromString(resultSet.getString("PLAYER_OWNERUUID")),
									chunk,
									resultSet.getString("LOADER_TYPE"),
									LocalDateTime.parse(resultSet.getString("LOADER_CREATIONTIME"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
									LocalDateTime.parse(resultSet.getString("LOADER_SHUTDOWNTIME"), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
							)
					);
				}

				if (chunkloaders.size() == 0) {
					Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), infoCallback::onNoChunkloaderFound);
					return;
				}

				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> infoCallback.onChunkloaderFound(chunkloaders));
			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not execute query!");
					e.printStackTrace();
				}
			}
		});
	}

	public static void updateShutdownTime(final UUID uniqueId, final ShutdownTimeCallback shutdownTimeCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement statement = connection.prepareStatement("UPDATE LOADER SET LOADER_SHUTDOWNTIME = ? WHERE PLAYER_OWNERUUID = ?")
			) {
				LocalDateTime shutdownTime = LocalDateTime.now().plusHours(Utilities.config.offlineLoader.offlineLoaderDuration);
				statement.setString(1, shutdownTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				statement.setString(2, uniqueId.toString());
				statement.executeUpdate();
				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> shutdownTimeCallback.onSuccess(shutdownTime));
			} catch (SQLException e) {
				if (Utilities.config.general.debug) {
					Utilities.log(Level.SEVERE, "Could not execute query!");
					e.printStackTrace();
				}
			}
		});
	}

	public static void getOfflineChunkloaders(final OfflineChunkloaderCallback offlineChunkloaderCallback) {
		Bukkit.getScheduler().runTaskAsynchronously(DirtLoader.getPlugin(), () -> {
			try (Connection connection = Database.getConnection();
			     PreparedStatement statement = connection.prepareStatement("SELECT * FROM LOADER WHERE LOADER_TYPE = 'offline'")
			) {
				ResultSet resultSet = statement.executeQuery();
				List<ChunkLoader> chunkloaders = new ArrayList<>();
				while (resultSet.next()) {
					chunkloaders.add(
							new ChunkLoader(
									UUID.fromString(resultSet.getString("LOADER_UUID")),
									UUID.fromString(resultSet.getString("PLAYER_OWNERUUID")),
									new Chunk(
											resultSet.getString("LOADER_WORLD").trim(),
											resultSet.getInt("LOADER_X"),
											resultSet.getInt("LOADER_Z")
									),
									resultSet.getString("LOADER_TYPE").trim(),
									LocalDateTime.parse(resultSet.getString("LOADER_CREATIONTIME"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
									LocalDateTime.parse(resultSet.getString("LOADER_SHUTDOWNTIME"), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
							)
					);
				}

				Bukkit.getScheduler().runTask(DirtLoader.getPlugin(), () -> offlineChunkloaderCallback.onSuccess(chunkloaders));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}
}