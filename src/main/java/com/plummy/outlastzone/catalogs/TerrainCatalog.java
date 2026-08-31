package com.plummy.outlastzone.catalogs;

import com.plummy.outlastzone.core.data.AbstractCatalog;
import com.plummy.outlastzone.core.data.Loadable;
import com.plummy.outlastzone.terrain.Terrain;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.structure.Structure;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.plummy.outlastzone.OutlastZone.getInstance;
import static com.plummy.outlastzone.OutlastZone.logger;

public class TerrainCatalog extends AbstractCatalog<String, Terrain> implements Loadable {

    @Override
    public void load() {
        clear();

        File file = new File(getInstance().getDataFolder(), "terrains.yml");

        if (!file.exists()) {
            getInstance().saveResource("terrains.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("terrains");

        if (section == null) {
            logger().warning("No terrains section found in " + file.getName());
            return;
        }

        for (String key : section.getKeys(false)) {
            Terrain terrain = loadTerrain(section.getConfigurationSection(key));

            if (terrain == null) {
                logger().warning("Invalid terrain: " + key);
                continue;
            }

            add(terrain);
        }
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
