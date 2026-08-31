package com.plummy.outlastzone.core.data.repositories;

import com.plummy.outlastzone.core.data.Repository;
import com.plummy.outlastzone.core.players.BasePlayer;

import java.util.Collection;
import java.util.UUID;

public interface PlayerRepository<V extends BasePlayer> extends Repository<UUID, V> {

    default Collection<V> allOnline() {
        return all().stream().filter(BasePlayer::isOnline).toList();
    }

    default void message(String message) {
        allOnline().forEach(player -> player.message(message));
    }

    default void messageToActionBar(String message) {
        allOnline().forEach(player -> player.messageToActionBar(message));
    }
}
