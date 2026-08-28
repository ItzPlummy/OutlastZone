package com.plummy.outlastzone.players;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ActivePlayer extends BasePlayer {

    @NotNull
    PersistentPlayer getPersistentPlayer();

    @NotNull
    ActivePlayerRole getRole();

    boolean isPlaying();

    boolean isSpectating();

    void prepareForSetup();

    void prepareForGame();

    void eliminate();

    @Override
    default @NotNull UUID getUUID() {
        return getPersistentPlayer().getUUID();
    }

    @Override
    default @NotNull String getName() {
        return getPersistentPlayer().getName();
    }

    @Override
    default Player getBukkitPlayer() {
        return getPersistentPlayer().getBukkitPlayer();
    }

    @Override
    default boolean isOnline() {
        return getPersistentPlayer().isOnline();
    }
}
