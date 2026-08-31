package com.plummy.outlastzone.visual.announcers;

import com.plummy.outlastzone.players.ActivePlayer;
import com.plummy.outlastzone.repositories.ActivePlayerRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static com.plummy.outlastzone.OutlastZone.getInstance;

public class DefaultGameAnnouncer implements GameAnnouncer {

    private static final long GAME_OVER_DELAY_TICKS = 20L;

    private final ActivePlayerRepository players;

    public DefaultGameAnnouncer(ActivePlayerRepository players) {
        this.players = players;
    }

    @Override
    public void locationNotReady() {
        players.messageToActionBar("§cLocation is not prepared. Try again later");
    }

    @Override
    public void gameStopped() {
        players.messageToActionBar("§cGame stopped");
    }

    @Override
    public void gameOver(ActivePlayer winner) {
        String title = "§cGame Over";
        String subtitle = winner == null ? "" : "§c" + winner.getName() + " outlasted everyone!";

        new BukkitRunnable() {

            @Override
            public void run() {
                for (ActivePlayer player : players.allOnline()) {
                    Player bukkitPlayer = player.getBukkitPlayer();

                    bukkitPlayer.showTitle(Title.title(Component.text(title).asComponent(), Component.text(subtitle).asComponent()));
                    bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f);
                }
            }
        }.runTaskLater(getInstance(), GAME_OVER_DELAY_TICKS);
    }
}
