package com.plummy.outlastzone.populators;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.plummy.outlastzone.OutlastZone.getInstance;
import static com.plummy.outlastzone.OutlastZone.logger;

public class OreEnhancementPopulator extends BlockPopulator {

    private static final Map<Material, List<Material>> ORE_ENHANCEMENTS = loadOreEnhancements();

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if (!getInstance().getConfig().getBoolean("grind-phase.enhancements.ore-generation-enhancement.enable", true)) return;

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        double replacementChance = getInstance().getConfig().getDouble("grind-phase.enhancements.ore-generation-enhancement.replacement-chance", 0.2);

        for (int x = baseX; x < baseX + 16; x++) {
            for (int z = baseZ; z < baseZ + 16; z++) {
                for (int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight(); y++) {
                    if (!limitedRegion.isInRegion(x, y, z) || random.nextDouble() > replacementChance) continue;

                    Material enhancedOre = getEnhancedOre(limitedRegion.getType(x, y, z), random.nextDouble());

                    if (enhancedOre != null) {
                        limitedRegion.setType(x, y, z, enhancedOre);
                    }
                }
            }
        }
    }

    public static void reloadOreEnhancements() {
        ORE_ENHANCEMENTS.clear();
        ORE_ENHANCEMENTS.putAll(loadOreEnhancements());
    }

    private static Map<Material, List<Material>> loadOreEnhancements() {
        Map<Material, List<Material>> enhancements = new HashMap<>();
        ConfigurationSection replacementsSection = getInstance().getConfig().getConfigurationSection("grind-phase.enhancements.ore-generation-enhancement");

        if (replacementsSection == null) {
            logger().warning("No ore-generation-enhancement configuration found.");
            return enhancements;
        }

        List<?> rawEntries = replacementsSection.getList("replacements");
        if (rawEntries == null) {
            logger().warning("No replacements list found for ore-generation-enhancement.");
            return enhancements;
        }

        for (Object rawEntry : rawEntries) {
            if (!(rawEntry instanceof List<?> entry) || entry.size() != 2) {
                logger().warning("Invalid ore replacement entry, expected [material, [materials]]: " + rawEntry);
                continue;
            }

            Material key = Material.matchMaterial(String.valueOf(entry.getFirst()));
            if (key == null) {
                logger().warning("Invalid base material in ore replacement entry: " + entry.getFirst());
                continue;
            }

            if (!(entry.get(1) instanceof List<?> rawTargets)) {
                logger().warning("Invalid replacement target list for " + key + ": " + entry.get(1));
                continue;
            }

            List<Material> targets = new ArrayList<>();
            for (Object rawTarget : rawTargets) {
                Material target = Material.matchMaterial(String.valueOf(rawTarget));
                if (target == null) {
                    logger().warning("Invalid replacement target for " + key + ": " + rawTarget);
                    continue;
                }
                targets.add(target);
            }

            if (targets.isEmpty()) {
                logger().warning("No valid replacement targets for " + key + ", skipping.");
                continue;
            }

            enhancements.put(key, targets);
        }

        return enhancements;
    }

    private static Material getEnhancedOre(Material material, double randomValue) {
        List<Material> oreEnhancements = ORE_ENHANCEMENTS.get(material);
        return oreEnhancements == null ? null : oreEnhancements.get((int) (randomValue * oreEnhancements.size()));
    }
}
