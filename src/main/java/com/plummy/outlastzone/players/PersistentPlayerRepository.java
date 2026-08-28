package com.plummy.outlastzone.players;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

import static com.plummy.outlastzone.OutlastZone.getInstance;
import static com.plummy.outlastzone.OutlastZone.logger;

public class PersistentPlayerRepository {

    private final LinkedHashMap<UUID, PersistentPlayer> players = new LinkedHashMap<>();

    public static PersistentPlayerRepository load() {
        PersistentPlayerRepository players = new PersistentPlayerRepository();

        File file = new File(getInstance().getDataFolder(), "players.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("players");

        if (section == null) {
            return players;
        }

        for (String uuid : section.getKeys(false)) {
            PersistentPlayer player = loadPlayer(section.getConfigurationSection(uuid));

            if (player == null) {
                continue;
            }

            players.addPlayer(player);
        }

        return players;
    }

    public void save() {
        File file = new File(getInstance().getDataFolder(), "players.yml");

        YamlConfiguration config = new YamlConfiguration();
        ConfigurationSection section = config.createSection("players");

        for (PersistentPlayer player : players.values()) {
            ConfigurationSection playerSection = section.createSection(player.getUUID().toString());
            savePlayer(playerSection, player);
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            logger().severe("Failed to save persistent players data to " + file.getName() + ": " + exception.getMessage());
        }
    }

    public void addPlayer(PersistentPlayer player) {
        players.putIfAbsent(player.getUUID(), player);
    }

    public PersistentPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public @NotNull Collection<PersistentPlayer> getAllPlayers() {
        return players.values();
    }

    public boolean hasPlayer(UUID uuid) {
        return players.containsKey(uuid);
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    protected static PersistentPlayer loadPlayer(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        UUID uuid;

        try {
            uuid = UUID.fromString(section.getName());
        } catch (IllegalArgumentException e) {
            return null;
        }

        String name = section.getString("name");

        if (name == null) {
            return null;
        }

        return new DefaultPersistentPlayer(
                uuid,
                name,
                section.getInt("gamesPlayed", 0),
                section.getInt("wins", 0),
                section.getInt("losses", 0),
                section.getInt("kills", 0),
                section.getInt("deaths", 0)
        );
    }

    protected static void savePlayer(@NotNull ConfigurationSection section, @NotNull PersistentPlayer player) {
        section.set("name", player.getName());
        section.set("gamesPlayed", player.getGamesPlayed());
        section.set("wins", player.getWins());
        section.set("losses", player.getLosses());
        section.set("kills", player.getKills());
        section.set("deaths", player.getDeaths());
    }
}
