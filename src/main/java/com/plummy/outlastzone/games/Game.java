package com.plummy.outlastzone.games;

import com.plummy.outlastzone.players.ActivePlayer;
import com.plummy.outlastzone.players.ActivePlayerRepository;
import com.plummy.outlastzone.terrain.Terrain;
import org.bukkit.Location;

public interface Game {

    GameStage getStage();

    Terrain getTerrain();

    ActivePlayerRepository getActivePlayers();

    Location getSpawnLocation();

    void start(ActivePlayer host);

    void startGrindStage();

    void startFightStage();

    void finish(GameFinishReason reason);

    default boolean hasStarted() {
        return getStage() != GameStage.IDLE;
    }

    default boolean hasFinished() {
        return getStage() == GameStage.FINISHED;
    }

    default boolean isSettingUp() {
        return getStage() == GameStage.SETUP;
    }
}
