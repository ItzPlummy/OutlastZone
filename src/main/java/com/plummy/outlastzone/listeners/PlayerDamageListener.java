package com.plummy.outlastzone.listeners;

import com.plummy.outlastzone.games.Game;
import com.plummy.outlastzone.games.GameStage;
import com.plummy.outlastzone.players.ActivePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static com.plummy.outlastzone.OutlastZone.getGameManager;

public class PlayerDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Game game = getGameManager().getGame();

        if (game != null && game.getStage() == GameStage.FIGHTING) {
            ActivePlayer activePlayer = game.getActivePlayers().getPlayer(player.getUniqueId());

            if (activePlayer != null && activePlayer.isPlaying()) {
                return;
            }
        }

        event.setCancelled(true);
    }
}
