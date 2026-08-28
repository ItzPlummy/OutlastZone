package com.plummy.outlastzone.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

import static com.plummy.outlastzone.OutlastZone.getPersistentPlayers;

public class ActivePlayerRepository {

    private final LinkedHashMap<UUID, ActivePlayer> players = new LinkedHashMap<>();

    public static ActivePlayerRepository create() {
        ActivePlayerRepository players = new ActivePlayerRepository();

        for (Player player : Bukkit.getOnlinePlayers()) {
            players.addPlayer(new DefaultActivePlayer(getPersistentPlayers().getPlayer(player.getUniqueId()), ActivePlayerRole.PLAYER));
        }

        return players;
    }

    public void addPlayer(ActivePlayer player) {
        players.putIfAbsent(player.getUUID(), player);
    }

    public ActivePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public @NotNull Collection<ActivePlayer> getAllPlayers() {
        return players.values();
    }

    public @NotNull Collection<ActivePlayer> getAllOnlinePlayers() {
        return getAllPlayers().stream().filter(ActivePlayer::isOnline).toList();
    }

    public @NotNull Collection<ActivePlayer> getParticipants() {
        return getAllOnlinePlayers().stream().filter(ActivePlayer::isPlaying).toList();
    }

    public @NotNull Collection<ActivePlayer> getSpectators() {
        return getAllOnlinePlayers().stream().filter(ActivePlayer::isSpectating).toList();
    }

    public int getParticipantsCount() {
        return getAllPlayers().stream().filter(ActivePlayer::isPlaying).toList().size();
    }

    public boolean hasPlayer(UUID uuid) {
        return players.containsKey(uuid);
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public void message(String message) {
        for (ActivePlayer player : getAllOnlinePlayers()) {
            player.message(message);
        }
    }

    public void messageToActionBar(String message) {
        for (ActivePlayer player : getAllOnlinePlayers()) {
            player.messageToActionBar(message);
        }
    }
}
