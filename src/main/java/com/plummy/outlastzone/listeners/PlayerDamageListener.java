package com.plummy.outlastzone.listeners;

import com.plummy.outlastzone.core.games.Game;
import com.plummy.outlastzone.core.games.GamePhase;
import com.plummy.outlastzone.core.players.ActivePlayer;
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

        if (game != null && game.getPhase() == GamePhase.FIGHTING) {
            ActivePlayer activePlayer = game.getPlayers().get(player.getUniqueId());

            if (activePlayer != null && activePlayer.isPlaying()) {
                return;
            }
        }

        event.setCancelled(true);
    }
}
