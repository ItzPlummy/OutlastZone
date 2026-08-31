package com.plummy.outlastzone.core.games;

import com.plummy.outlastzone.core.games.arena.Arena;
import com.plummy.outlastzone.core.players.ActivePlayer;
import com.plummy.outlastzone.core.data.repositories.ActivePlayerRepository;
import com.plummy.outlastzone.core.keyed.Terrain;
import com.plummy.outlastzone.core.visual.bossBars.BossBarDisplay;
import org.bukkit.Location;

public interface Game {

    GamePhase getPhase();

    Terrain getTerrain();

    ActivePlayerRepository getPlayers();

    Arena getArena();

    BossBarDisplay getActiveBossBar();

    void start(ActivePlayer host);

    void startGrindPhase();

    void startFightPhase();

    void eliminate(ActivePlayer player);

    void finish(GameFinishReason reason);

    default Location getSpawnLocation() {
        return getArena() == null ? null : getArena().getSpawnLocation();
    }

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
