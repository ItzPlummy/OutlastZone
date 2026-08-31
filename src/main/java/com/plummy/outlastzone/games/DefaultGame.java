package com.plummy.outlastzone.games;

import com.plummy.outlastzone.players.ActivePlayer;
import com.plummy.outlastzone.repositories.ActivePlayerRepository;
import com.plummy.outlastzone.terrain.Terrain;
import com.plummy.outlastzone.visual.DisplayWheelRegistry;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.*;

public class DefaultGame implements Game {

    private final Terrain terrain;
    private final ActivePlayerRepository activePlayers;

    private final BossBar grindCountdownBossBar = BossBar.bossBar(
            Component.text("Grind Phase"),
            0,
            BossBar.Color.YELLOW,
            BossBar.Overlay.PROGRESS
    );

    private final BossBar fightBossBar = BossBar.bossBar(
            Component.text("Fight Phase"),
            1,
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS
    );

    private GamePhase phase = GamePhase.IDLE;

    private Location spawnLocation = null;
    private BukkitTask grindCountdownTask = null;
    private BukkitTask fightTask = null;

    public DefaultGame(Terrain terrain) {
        this.terrain = terrain;
        this.activePlayers = new ActivePlayerRepository();

        activePlayers.load();
    }

    @Override
    public GamePhase getPhase() {
        return phase;
    }

    @Override
    public Terrain getTerrain() {
        return terrain;
    }

    @Override
    public ActivePlayerRepository getPlayers() {
        return activePlayers;
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public void start(ActivePlayer host) {
        setPhase(GamePhase.LOCATING);

        Location spawnLocation = getSpawnLocationPool().pop(getTerrain());

        if (spawnLocation == null) {
            getPlayers().messageToActionBar("§cLocation is not prepared. Try again later");
            finish(GameFinishReason.NO_LOCATION_FOUND);
            return;
        }

        setSpawnLocation(spawnLocation);

        setPhase(GamePhase.SETUP);

        for (ActivePlayer player : getPlayers().allOnlineParticipants()) {
            int maxDistanceFromSpawn = getSettings().getBorderSize(getPlayers().onlineParticipantsCount()) / 2;
            double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2;

            Location playerSpawn = getSpawnLocation().clone().add((int) Math.cos(angle) * maxDistanceFromSpawn, 0, (int) Math.sin(angle) * maxDistanceFromSpawn);
            playerSpawn.setY(getSpawnLocation().getWorld().getHighestBlockYAt(playerSpawn) + 51);
            player.prepareForSetup();

            player.getBukkitPlayer().teleport(playerSpawn);
        }

        prepareWorldForSetup();

        new BukkitRunnable() {

            @Override
            public void run() {
                for (ActivePlayer player : getPlayers().allOnlineParticipants()) {
                    DisplayWheelRegistry.TERRAIN_DISPLAY_WHEEL.reveal(player, getTerrain(), () -> {});
                }
            }
        }.runTaskLater(getInstance(), 40L);

        new BukkitRunnable() {

            @Override
            public void run() {
                endSetup();
            }
        }.runTaskLater(getInstance(), 190L);
    }

    @Override
    public void startGrindPhase() {
        setPhase(GamePhase.GRINDING);

        if (grindCountdownTask != null) grindCountdownTask.cancel();
        grindCountdownTask = null;

        if (fightTask != null) fightTask.cancel();
        fightTask = null;

        int durationSeconds = getSettings().getGrindStageDurationSeconds();

        grindCountdownBossBar.name(Component.text(getGrindCountdownBossBarName(0, durationSeconds)));
        grindCountdownBossBar.progress(0);

        for (ActivePlayer player : getPlayers().allOnline()) {
            player.getBukkitPlayer().removePotionEffect(PotionEffectType.GLOWING);
            player.getBukkitPlayer().hideBossBar(fightBossBar);
            player.getBukkitPlayer().showBossBar(grindCountdownBossBar);
        }

        grindCountdownTask = new BukkitRunnable() {

            private int timer = 0;

            @Override
            public void run() {
                timer++;

                grindCountdownBossBar.progress((float) timer / durationSeconds);
                grindCountdownBossBar.name(Component.text(getGrindCountdownBossBarName(timer, durationSeconds)));

                for (ActivePlayer player : getPlayers().allOnline()) {
                    player.getBukkitPlayer().hideBossBar(fightBossBar);
                    player.getBukkitPlayer().showBossBar(grindCountdownBossBar);
                }

                if (timer >= durationSeconds) {
                    startFightPhase();
                }
            }
        }.runTaskTimer(getInstance(), 20L, 20L);
    }

    @Override
    public void startFightPhase() {
        setPhase(GamePhase.FIGHTING);

        int newBorderSize = getSettings().getBorderSize(getPlayers().onlineParticipantsCount() - 1);
        int shrinkDuration = getSettings().getFightStageBorderNarrowingDurationSeconds() * 20;

        getSpawnLocation().getWorld().getWorldBorder().changeSize(newBorderSize, shrinkDuration);

        if (grindCountdownTask != null) grindCountdownTask.cancel();
        grindCountdownTask = null;

        if (fightTask != null) fightTask.cancel();
        fightTask = null;

        fightBossBar.name(Component.text(getFightBossBarName(0)));
        fightBossBar.progress(1);

        for (ActivePlayer player : getPlayers().allOnline()) {
            player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999999, 0, false, false, false));
            player.getBukkitPlayer().hideBossBar(grindCountdownBossBar);
            player.getBukkitPlayer().showBossBar(fightBossBar);
        }

        fightTask = new BukkitRunnable() {

            private int timer = 0;

            @Override
            public void run() {
                timer++;

                fightBossBar.progress(1);
                fightBossBar.name(Component.text(getFightBossBarName(timer)));

                for (ActivePlayer player : getPlayers().allOnline()) {
                    player.getBukkitPlayer().hideBossBar(grindCountdownBossBar);
                    player.getBukkitPlayer().showBossBar(fightBossBar);
                }
            }
        }.runTaskTimer(getInstance(), 20L, 20L);
    }

    @Override
    public void finish(GameFinishReason reason) {
        setPhase(GamePhase.FINISHED);

        if (grindCountdownTask != null) grindCountdownTask.cancel();
        grindCountdownTask = null;

        if (fightTask != null) fightTask.cancel();
        fightTask = null;

        if (reason == GameFinishReason.STOP_COMMAND_EXECUTED) {
            for (ActivePlayer player : getPlayers().allOnline()) {
                Player bukkitPLayer = player.getBukkitPlayer();

                bukkitPLayer.hideBossBar(grindCountdownBossBar);
                bukkitPLayer.hideBossBar(fightBossBar);

                player.messageToActionBar("§cGame stopped");
            }

            return;
        }

        if (reason == GameFinishReason.NO_LOCATION_FOUND) {
            return;
        }

        String title = "§cGame Over";
        String subtitle;

        if (getPlayers().onlineParticipantsCount() == 1) {
            ActivePlayer lastPlayer = getPlayers().allOnlineParticipants().stream().findAny().orElseThrow();
            subtitle = "§c" + lastPlayer.getName() + " outlasted everyone!";
        } else {
            subtitle = "";
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                for (ActivePlayer player : getPlayers().allOnline()) {
                    Player bukkitPLayer = player.getBukkitPlayer();

                    bukkitPLayer.hideBossBar(grindCountdownBossBar);
                    bukkitPLayer.hideBossBar(fightBossBar);

                    bukkitPLayer.showTitle(Title.title(Component.text(title).asComponent(), Component.text(subtitle).asComponent()));
                    bukkitPLayer.playSound(player.getBukkitPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f);
                }
            }
        }.runTaskLater(getInstance(), 20L);

        getGameManager().removeGame();
    }

    protected void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    protected void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    protected void prepareWorldForSetup() {
        World world = getSpawnLocation().getWorld();

        world.setTime(1000);
        world.setStorm(false);
        world.setThundering(false);

        world.getWorldBorder().setCenter(getSpawnLocation());
        world.getWorldBorder().setSize(999999);
        world.getWorldBorder().setDamageBuffer(0);

        world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
    }

    protected void prepareWorldForGame() {
        World world = getSpawnLocation().getWorld();
        world.getWorldBorder().setSize(getSettings().getBorderSize(getPlayers().onlineParticipantsCount()));
    }

    protected void endSetup() {
        for (ActivePlayer player : getPlayers().allOnlineParticipants()) {
            player.prepareForGame();
        }

        prepareWorldForGame();
        startGrindPhase();
    }

    protected String getGrindCountdownBossBarName(int timer, int duration) {
        int total = Math.max(0, duration - timer);
        return "§e§lGrind Phase§e | " + String.format("%02d:%02d", total / 60, total % 60) + " left";
    }

    protected String getFightBossBarName(int timer) {
        int total = Math.max(0, timer);
        return "§c§lFight Phase§c | " + String.format("%02d:%02d", total / 60, total % 60);
    }
}
