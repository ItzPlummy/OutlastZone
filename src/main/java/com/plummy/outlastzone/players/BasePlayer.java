package com.plummy.outlastzone.players;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface BasePlayer {

    @NotNull
    UUID getUUID();

    @NotNull
    String getName();

    Player getBukkitPlayer();

    boolean isOnline();

    default void message(String message) {
        if (isOnline()) getBukkitPlayer().sendMessage(message);
    }

    default void messageToActionBar(String message) {
        if (isOnline()) getBukkitPlayer().sendActionBar(Component.text(message));
    }
}
