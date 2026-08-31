package com.plummy.outlastzone.core.visual.bossBars;

import com.plummy.outlastzone.core.players.BasePlayer;

import java.util.Collection;

public interface BossBarDisplay {

    void update(int elapsed);

    void showTo(BasePlayer player);

    void hideFrom(BasePlayer player);

    default void showTo(Collection<? extends BasePlayer> players) {
        players.forEach(this::showTo);
    }

    default void hideFrom(Collection<? extends BasePlayer> players) {
        players.forEach(this::hideFrom);
    }
}
