package com.plummy.outlastzone.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultPersistentPlayer implements PersistentPlayer {

    @NotNull
    private final UUID uuid;

    @NotNull
    private final String name;

    private int gamesPlayed;
    private int wins;
    private int losses;
    private int kills;
    private int deaths;

    public DefaultPersistentPlayer(@NotNull UUID uuid, @NotNull String name, int gamesPlayed, int wins, int losses, int kills, int deaths) {
        this.uuid = uuid;
        this.name = name;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.kills = kills;
        this.deaths = deaths;
    }

    public static PersistentPlayer fromBukkitPlayer(Player player) {
        return new DefaultPersistentPlayer(player.getUniqueId(), player.getName(), 0, 0, 0, 0, 0);
    }

    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public boolean isOnline() {
        return getBukkitPlayer() != null;
    }

    @Override
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    @Override
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    @Override
    public int getWins() {
        return wins;
    }

    @Override
    public void setWins(int wins) {
        this.wins = wins;
    }

    @Override
    public int getLosses() {
        return losses;
    }

    @Override
    public void setLosses(int losses) {
        this.losses = losses;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public int getDeaths() {
        return deaths;
    }

    @Override
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}
