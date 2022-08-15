CREATE TABLE IF NOT EXISTS player
(
    player_uuid             CHAR(36)    NOT NULL,
    player_name             VARCHAR(20) NOT NULL,
    player_onlineAvailable  tinyint     NOT NULL,
    player_offlineAvailable tinyint     NOT NULL,
    player_onlineUsed       tinyint     NOT NULL,
    player_offlineUsed      tinyint     NOT NULL,
    primary key (player_uuid)
);

CREATE TABLE IF NOT EXISTS loader
(
    loader_uuid         CHAR(36)    NOT NULL,
    player_ownerUuid    CHAR(36)    NOT NULL,
    loader_type         CHAR(8)     NOT NULL,
    loader_world        CHAR(36)    NOT NULL,
    loader_x            SMALLINT    NOT NULL,
    loader_z            SMALLINT    NOT NULL,
    loader_creationTime VARCHAR(40) NOT NULL,
    loader_shutdownTime VARCHAR(40),
    primary key (loader_uuid),
    FOREIGN KEY (player_ownerUuid) REFERENCES player (player_uuid) ON DELETE CASCADE
);