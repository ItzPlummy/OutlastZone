package com.plummy.outlastzone.games;

import com.plummy.outlastzone.terrain.Terrain;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.plummy.outlastzone.OutlastZone.getTerrains;

public class GameManager {

    private Game game = null;

    public void startGame(@NotNull Player player, Terrain terrain) {
        if (terrain == null) {
            terrain = getTerrains().selectRandomTerrain();
        }

        game = new DefaultGame(terrain);
        game.start(game.getActivePlayers().getPlayer(player.getUniqueId()));
    }

    public Game getGame() {
        return game;
    }

    public void finishGame(GameFinishReason reason) {
        game.finish(reason);
    }

    public void removeGame() {
        game = null;
    }
}
