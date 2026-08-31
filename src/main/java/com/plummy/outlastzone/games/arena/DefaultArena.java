package com.plummy.outlastzone.games.arena;

import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.getSettings;

public class DefaultArena implements Arena {

    private static final long SETUP_TIME = 1000;
    private static final double OPEN_BORDER_SIZE = 999999;
    private static final int SCATTER_HEIGHT_OFFSET = 51;

    private final Location spawnLocation;

    public DefaultArena(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public World getWorld() {
        return getSpawnLocation().getWorld();
    }

    @Override
    public void prepareForSetup() {
        World world = getWorld();

        world.setTime(SETUP_TIME);
        world.setStorm(false);
        world.setThundering(false);

        world.getWorldBorder().setCenter(getSpawnLocation());
        world.getWorldBorder().setSize(OPEN_BORDER_SIZE);
        world.getWorldBorder().setDamageBuffer(0);

        world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
    }

    @Override
    public void closeBorder(int playerCount) {
        getWorld().getWorldBorder().setSize(getSettings().getBorderSize(playerCount));
    }

    @Override
    public void shrinkBorder(int playerCount, int durationTicks) {
        getWorld().getWorldBorder().changeSize(getSettings().getBorderSize(playerCount), durationTicks);
    }

    @Override
    public Location scatterPoint(int playerCount) {
        int maxDistanceFromSpawn = getSettings().getBorderSize(playerCount) / 2;
        double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2;

        Location scatterPoint = getSpawnLocation().clone().add((int) Math.cos(angle) * maxDistanceFromSpawn, 0, (int) Math.sin(angle) * maxDistanceFromSpawn);
        scatterPoint.setY(getWorld().getHighestBlockYAt(scatterPoint) + SCATTER_HEIGHT_OFFSET);

        return scatterPoint;
    }
}
