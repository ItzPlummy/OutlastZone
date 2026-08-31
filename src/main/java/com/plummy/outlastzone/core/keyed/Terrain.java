package com.plummy.outlastzone.core.keyed;

import com.plummy.outlastzone.core.visual.displays.AbstractKeyedDisplayable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.generator.structure.Structure;

import java.util.Map;
import java.util.Set;

public class Terrain extends AbstractKeyedDisplayable<String, BlockDisplay> {

    private final Set<Biome> biomes;
    private final Map<Structure, Set<Biome>> structures;

    public Terrain(String key, String name, Material item, Set<Biome> biomes, Map<Structure, Set<Biome>> structures) {
        super(key, name, item);
        this.biomes = biomes;
        this.structures = structures;
    }

    public Set<Biome> getBiomes() {
        return biomes;
    }

    public Map<Structure, Set<Biome>> getStructures() {
        return structures;
    }

    public Set<Biome> getStructureBiomes(Structure structure) {
        return structures.getOrDefault(structure, Set.of());
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
