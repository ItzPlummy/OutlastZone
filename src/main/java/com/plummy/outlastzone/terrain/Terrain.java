package com.plummy.outlastzone.terrain;

import com.plummy.outlastzone.visual.AbstractKeyedDisplayable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.generator.structure.Structure;

import java.util.List;
import java.util.Set;

public class Terrain extends AbstractKeyedDisplayable<BlockDisplay> {

    private final Set<Biome> biomes;
    private final List<Structure> structures;

    public Terrain(String key, String name, Material item, Set<Biome> biomes, List<Structure> structures) {
        super(key, name, item);
        this.biomes = biomes;
        this.structures = structures;
    }

    public Set<Biome> getBiomes() {
        return biomes;
    }

    public List<Structure> getStructures() {
        return structures;
    }

    @Override
    public BlockDisplay spawn(Location location) {
        Location normalizedLocation = location.clone().setRotation(0, 0);

        return normalizedLocation.getWorld().spawn(
                location.clone(),
                BlockDisplay.class,
                entity -> {
                    entity.setBlock(getItem().createBlockData());
                    entity.setInterpolationDuration(1);
                    entity.setInterpolationDelay(0);
                    entity.setPersistent(false);
                    entity.setVisibleByDefault(false);
                }
        );
    }
}
