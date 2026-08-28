package com.plummy.outlastzone.locationFinders;

import com.plummy.outlastzone.terrain.Terrain;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.structure.Structure;
import org.bukkit.util.StructureSearchResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.getInstance;

public class LocationFinder {

    private static final int OPTIMAL_Y = 64;

    private final int outerRadius;
    private final int searchRadius;
    private final int innerRadius;

    public LocationFinder(int outerRadius, int searchRadius, int innerRadius) {
        this.outerRadius = outerRadius;
        this.searchRadius = searchRadius;
        this.innerRadius = innerRadius;
    }

    public Location findLocation(World world, Terrain terrain) {
        int tries = 0;

        while (tries < getInstance().getConfig().getInt("terrain-search.max-tries", 10)) {
            tries++;

            Location location = findSuitableStructure(world, terrain);

            if (location == null) {
                continue;
            }

            location = approximateTerrainCenter(location, terrain);

            if (!verifyTerrainSuitability(location, terrain)) {
                continue;
            }

            return new Location(location.getWorld(), location.getBlockX() + 0.5, OPTIMAL_Y, location.getBlockZ() + 0.5);
        }

        return null;
    }

    protected @NotNull Location createRandomLocation(World world) {
        return new Location(world, ThreadLocalRandom.current().nextInt(-outerRadius, outerRadius), OPTIMAL_Y, ThreadLocalRandom.current().nextInt(-outerRadius, outerRadius));
    }

    protected Location findSuitableStructure(World world, Terrain terrain) {
        Location origin = createRandomLocation(world);

        for (Structure structure : terrain.getStructures()) {
            StructureSearchResult searchResult = world.locateNearestStructure(origin, structure, searchRadius / 16, false);

            if (searchResult == null || searchResult.getLocation().distance(origin) > searchRadius) {
                continue;
            }

            return searchResult.getLocation();
        }

        return null;
    }

    protected @NotNull Location approximateTerrainCenter(Location location, Terrain terrain) {
        long xSum = 0;
        long zSum = 0;
        int count = 0;

        int gridSpacing = getInstance().getConfig().getInt("terrain-search.grid-spacing", 8);

        for (int x = location.getBlockX() - innerRadius * 2; x < location.getBlockX() + innerRadius * 2; x += gridSpacing) {
            for (int z = location.getBlockZ() - innerRadius * 2; z < location.getBlockZ() + innerRadius * 2; z += gridSpacing) {
                if (terrain.getBiomes().contains(location.getWorld().getComputedBiome(x, OPTIMAL_Y, z))) {
                    xSum += x;
                    zSum += z;
                    count++;
                }
            }
        }

        if (count == 0) {
            return location;
        }

        return new Location(location.getWorld(), Math.round((double) xSum / count), OPTIMAL_Y, Math.round((double) zSum / count));
    }

    protected boolean verifyTerrainSuitability(Location location, Terrain terrain) {
        int count = 0;
        int wholeCount = 0;

        int gridSpacing = getInstance().getConfig().getInt("terrain-search.grid-spacing", 8);

        for (int x = location.getBlockX() - innerRadius; x < location.getBlockX() + innerRadius; x += gridSpacing) {
            for (int z = location.getBlockZ() - innerRadius; z < location.getBlockZ() + innerRadius; z += gridSpacing) {
                wholeCount++;

                if (terrain.getBiomes().contains(location.getWorld().getComputedBiome(x, OPTIMAL_Y, z))) {
                    count++;
                }
            }
        }

        return count > wholeCount / 2;
    }
}
