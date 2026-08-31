package com.plummy.outlastzone.core.arenas;

import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.getSettings;

public class DefaultArena implements Arena {

    private final Location spawnLocation;

    public DefaultArena(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public void prepareForSetup() {
        World world = getWorld();

        world.setTime(1000);
        world.setStorm(false);
        world.setThundering(false);

        world.getWorldBorder().setCenter(getSpawnLocation());
        world.getWorldBorder().setSize(10000000);
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
        scatterPoint.setY(getWorld().getHighestBlockYAt(scatterPoint) + 51);

        return scatterPoint;
    }
}
