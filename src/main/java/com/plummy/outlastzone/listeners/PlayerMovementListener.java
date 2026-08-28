package com.plummy.outlastzone.listeners;

import com.plummy.outlastzone.games.Game;
import com.plummy.outlastzone.players.ActivePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.plummy.outlastzone.OutlastZone.getGameManager;

public class PlayerMovementListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Game game = getGameManager().getGame();

        if (game != null && game.isSettingUp()) {
            ActivePlayer player = game.getActivePlayers().getPlayer(event.getPlayer().getUniqueId());

            if (player != null && player.isPlaying()) {
                event.setCancelled(true);
            }
        }
    }
}
