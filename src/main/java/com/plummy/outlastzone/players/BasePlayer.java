package com.plummy.outlastzone.players;

import com.plummy.outlastzone.core.Keyed;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface BasePlayer extends Keyed<UUID> {

    @NotNull
    UUID getUUID();

    @NotNull
    String getName();

    Player getBukkitPlayer();

    boolean isOnline();

    @Override
    default UUID getKey() {
        return getUUID();
    }

    default void message(String message) {
        if (isOnline()) getBukkitPlayer().sendMessage(message);
    }

    default void messageToActionBar(String message) {
        if (isOnline()) getBukkitPlayer().sendActionBar(Component.text(message));
    }
}
