package com.plummy.outlastzone.core.workers.spawnLocation;

import org.bukkit.Location;
import org.bukkit.block.Biome;

import java.util.Set;

public class OriginSearchContext {

    private Location randomOrigin = null;
    private Set<Biome> biomes = null;

    public Location getRandomOrigin() {
        return randomOrigin;
    }

    public void setRandomOrigin(Location randomOrigin) {
        this.randomOrigin = randomOrigin;
    }

    public Set<Biome> getBiomes() {
        return biomes;
    }

    public void setBiomes(Set<Biome> biomes) {
        this.biomes = biomes;
    }
}
