package com.plummy.outlastzone.listeners;

import com.plummy.outlastzone.games.Game;
import com.plummy.outlastzone.players.ActivePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static com.plummy.outlastzone.OutlastZone.getGameManager;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Game game = getGameManager().getGame();
        if (game == null) return;

        ActivePlayer activePlayer = game.getPlayers().get(event.getPlayer().getUniqueId());
        if (activePlayer == null || !activePlayer.isPlaying()) return;

        game.eliminate(activePlayer);

        Component deathMessage = event.deathMessage();

        if (deathMessage != null) {
            String newDeathMessage = "§c" + PlainTextComponentSerializer.plainText().serialize(deathMessage) + " and is eliminated";
            event.deathMessage(Component.text(newDeathMessage).asComponent());
        }
    }
}
