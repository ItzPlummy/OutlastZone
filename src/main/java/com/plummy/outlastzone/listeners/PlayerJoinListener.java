package com.plummy.outlastzone.listeners;

import com.plummy.outlastzone.games.Game;
import com.plummy.outlastzone.players.ActivePlayerRole;
import com.plummy.outlastzone.players.DefaultActivePlayer;
import com.plummy.outlastzone.players.DefaultPersistentPlayer;
import com.plummy.outlastzone.players.PersistentPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.plummy.outlastzone.OutlastZone.getGameManager;
import static com.plummy.outlastzone.OutlastZone.getPersistentPlayers;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!getPersistentPlayers().has(event.getPlayer().getUniqueId())) {
            getPersistentPlayers().add(DefaultPersistentPlayer.fromBukkitPlayer(event.getPlayer()));
        }

        Game game = getGameManager().getGame();
        PersistentPlayer player = getPersistentPlayers().get(event.getPlayer().getUniqueId());

        if (game != null && !game.getPlayers().has(player.getUUID())) {
            game.getPlayers().add(new DefaultActivePlayer(player, ActivePlayerRole.SPECTATOR));
            player.getBukkitPlayer().teleport(game.getSpawnLocation());
        }
    }
}
