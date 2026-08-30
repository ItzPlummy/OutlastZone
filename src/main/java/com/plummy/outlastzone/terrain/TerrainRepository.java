package com.plummy.outlastzone.terrain;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.getInstance;
import static com.plummy.outlastzone.OutlastZone.logger;

public class TerrainRepository {

    private final LinkedHashMap<String, Terrain> terrains = new LinkedHashMap<>();

    public static TerrainRepository load() {
        TerrainRepository controller = new TerrainRepository();

        File file = new File(getInstance().getDataFolder(), "terrains.yml");

        if (!file.exists()) {
            getInstance().saveResource("terrains.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("terrains");

        if (section == null) {
            logger().warning("No terrains section found in " + file.getName());
            return controller;
        }

        for (String key : section.getKeys(false)) {
            Terrain terrain = loadTerrain(section.getConfigurationSection(key));

            if (terrain == null) {
                logger().warning("Invalid terrain: " + key);
                continue;
            }

            controller.addTerrain(terrain);
        }

        return controller;
    }

    private void addTerrain(Terrain terrain) {
        terrains.putIfAbsent(terrain.getKey(), terrain);
    }

    public Terrain getTerrain(String key) {
        return terrains.get(key);
    }

    public Collection<Terrain> getAllTerrains() {
        return terrains.values();
    }

    public @NotNull Terrain selectRandomTerrain() {
        List<String> keys = new ArrayList<>(terrains.keySet());
        return getTerrain(keys.get(ThreadLocalRandom.current().nextInt(keys.size())));
    }

    protected static Terrain loadTerrain(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        String name = section.getString("name");
        String itemName = section.getString("item");

        if (name == null || itemName == null) {
            return null;
        }

        Material item = Material.matchMaterial(itemName);

        if (item == null) {
            logger().warning("Invalid item in terrains: " + itemName);
            return null;
        }

        Set<Biome> biomes = new HashSet<>();

        for (String biomeName : section.getStringList("biomes")) {
            Biome biome = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).get(NamespacedKey.minecraft(biomeName.toLowerCase().replace("-", "_")));

            if (biome == null) {
                logger().warning("Invalid biome in terrains: " + biomeName);
                continue;
            }

            biomes.add(biome);
        }

        if (biomes.isEmpty()) {
            return null;
        }

        Map<Structure, Set<Biome>> structures = new HashMap<>();
        ConfigurationSection structuresSection = section.getConfigurationSection("structures");

        if (structuresSection != null) {
            for (String structureName : structuresSection.getKeys(false)) {
                Structure structure = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).get(NamespacedKey.minecraft(structureName.toLowerCase().replace("-", "_")));

                if (structure == null) {
                    logger().warning("Invalid structure in terrains: " + structureName);
                    continue;
                }

                Set<Biome> structureBiomes = new HashSet<>();

                for (String biomeName : structuresSection.getStringList(structureName)) {
                    Biome biome = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).get(NamespacedKey.minecraft(biomeName.toLowerCase().replace("-", "_")));

                    if (biome == null) {
                        logger().warning("Invalid biome in terrains: " + biomeName);
                        continue;
                    }

                    structureBiomes.add(biome);
                }

                if (structureBiomes.isEmpty()) {
                    logger().warning("Biome list is empty: " + structureName);
                    continue;
                }

                structures.put(structure, structureBiomes);
            }
        }

        return new Terrain(section.getName(), name, item, biomes, structures);
    }
}
