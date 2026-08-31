package com.plummy.outlastzone.games;

import com.plummy.outlastzone.players.ActivePlayer;
import com.plummy.outlastzone.repositories.ActivePlayerRepository;
import com.plummy.outlastzone.terrain.Terrain;
import org.bukkit.Location;

public interface Game {

    GamePhase getPhase();

    Terrain getTerrain();

    ActivePlayerRepository getPlayers();

    Location getSpawnLocation();

    void start(ActivePlayer host);

    void startGrindPhase();

    void startFightPhase();

    void finish(GameFinishReason reason);

    default boolean hasStarted() {
        return getPhase() != GamePhase.IDLE;
    }

    default boolean hasFinished() {
        return getPhase() == GamePhase.FINISHED;
    }

    default boolean isSettingUp() {
        return getPhase() == GamePhase.SETUP;
    }
}
