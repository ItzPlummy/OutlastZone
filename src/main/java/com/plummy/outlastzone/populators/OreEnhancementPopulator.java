package com.plummy.outlastzone.populators;

import com.plummy.outlastzone.OutlastZone;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Set;

public class OreEnhancementPopulator extends BlockPopulator {

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        if (!OutlastZone.getSettings().isOreGenerationEnhancementEnabled()) return;

        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        double replacementChance = OutlastZone.getSettings().getOreGenerationEnhancementReplacementChance();

        for (int x = baseX; x < baseX + 16; x++) {
            for (int z = baseZ; z < baseZ + 16; z++) {
                for (int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight(); y++) {
                    if (!limitedRegion.isInRegion(x, y, z) || random.nextDouble() > replacementChance) continue;

                    Material enhancedOre = getEnhancedOre(limitedRegion.getType(x, y, z), random);

                    if (enhancedOre != null) {
                        limitedRegion.setType(x, y, z, enhancedOre);
                    }
                }
            }
        }
    }

    private static Material getEnhancedOre(Material material, Random random) {
        Set<Material> candidates = OutlastZone.getSettings().getOreGenerationEnhancementReplacements(material);
        if (candidates.isEmpty()) return null;

        int index = random.nextInt(candidates.size());
        int i = 0;
        for (Material candidate : candidates) {
            if (i++ == index) return candidate;
        }

        return null;
    }
}
