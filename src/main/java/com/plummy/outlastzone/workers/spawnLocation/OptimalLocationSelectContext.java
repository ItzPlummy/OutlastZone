package com.plummy.outlastzone.workers.spawnLocation;

import org.bukkit.Location;
import org.bukkit.block.Biome;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OptimalLocationSelectContext {

    private Location origin = null;
    private Location currentSelection = null;
    private int x = 0;
    private int z = 0;
    private int wholeCount = 0;
    private int suitableCount = 0;

    private final Map<Long, Boolean> computedBiomeSuitability = new HashMap<>();
    private final Map<Location, Double> locationSuitabilityScores = new HashMap<>();

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(Location currentSelection) {
        this.currentSelection = currentSelection;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean xIsWithinSearchRadius(int searchRadius) {
        return x <= getCurrentSelection().getBlockX() + searchRadius;
    }

    public void resetX(int searchRadius) {
        setX(getCurrentSelection().getBlockX() - searchRadius);
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean zIsWithinSearchRadius(int searchRadius) {
        return z <= getCurrentSelection().getBlockZ() + searchRadius;
    }

    public void resetZ(int searchRadius) {
        setZ(getCurrentSelection().getBlockZ() - searchRadius);
    }

    public int getSuitableCount() {
        return suitableCount;
    }

    public void setSuitableCount(int suitableCount) {
        this.suitableCount = suitableCount;
    }

    public int getWholeCount() {
        return wholeCount;
    }

    public void setWholeCount(int wholeCount) {
        this.wholeCount = wholeCount;
    }

    public Boolean getBiomeSuitability(int y, Set<Biome> biomes) {
        long key = ((long) getX() << 32) | (getZ() & 0xFFFFFFFFL);
        return computedBiomeSuitability.computeIfAbsent(key, k -> biomes.contains(getOrigin().getWorld().getComputedBiome(getX(), y, getZ())));
    }

    public void addSuitabilityScore(Location location, Double score) {
        locationSuitabilityScores.put(location, score);
    }

    public int getCheckedLocationsCount() {
        return locationSuitabilityScores.size();
    }

    public Location getOptimalLocation(double threshold) {
        Location optimalLocation = null;
        double optimalScore = 0;

        for (Map.Entry<Location, Double> entry : locationSuitabilityScores.entrySet()) {
            if (entry.getValue() != null && entry.getValue() >= threshold && optimalScore < entry.getValue()) {
                optimalLocation = entry.getKey();
                optimalScore = entry.getValue();
            }
        }

        return optimalLocation;
    }
}
