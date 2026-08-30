package com.plummy.outlastzone.core;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.plummy.outlastzone.OutlastZone.logger;

public class Settings {

    private FileConfiguration config;
    private Map<Material, Set<Material>> oreGenerationEnhancementReplacements;

    public Settings(FileConfiguration config) {
        reload(config);
    }

    public void reload(FileConfiguration config) {
        this.config = config;
        this.oreGenerationEnhancementReplacements = loadOreGenerationEnhancementReplacements();
    }

    public int getMinPlayers() {
        return config.getInt("min-players");
    }

    public int getBorderRadiusPerPlayer() {
        return config.getInt("border-radius-per-player");
    }

    public int getBorderSize(int playerCount) {
        return getBorderRadiusPerPlayer() * playerCount * 2 + 1;
    }

    public int getTerrainSearchOptimalY() {
        return config.getInt("terrain-search.optimal-y");
    }

    public int getTerrainSearchLocationsPerTerrain() {
        return config.getInt("terrain-search.locations-per-terrain");
    }

    public long getTerrainSearchExecutionTimeoutMillis() {
        return config.getLong("terrain-search.execution-timeout-millis");
    }

    public int getOriginSearchOuterRadius() {
        return config.getInt("terrain-search.origin-search.outer-radius");
    }

    public int getOriginSearchInnerRadius() {
        return config.getInt("terrain-search.origin-search.inner-radius");
    }

    public int getStructureSearchRadius() {
        return config.getInt("terrain-search.structure-search.radius");
    }

    public int getStructureSearchDelta() {
        return config.getInt("terrain-search.structure-search.delta");
    }

    public int getOptimalSelectionAttempts() {
        return config.getInt("terrain-search.optimal-selection.attempts");
    }

    public int getOptimalSelectionMinOffset() {
        return config.getInt("terrain-search.optimal-selection.min-offset");
    }

    public int getOptimalSelectionMaxOffset() {
        return config.getInt("terrain-search.optimal-selection.max-offset");
    }

    public int getOptimalSelectionRadius() {
        return config.getInt("terrain-search.optimal-selection.radius");
    }

    public int getOptimalSelectionDelta() {
        return config.getInt("terrain-search.optimal-selection.delta");
    }

    public double getOptimalSelectionThreshold() {
        return config.getDouble("terrain-search.optimal-selection.threshold");
    }

    public int getGrindStageDurationSeconds() {
        return config.getInt("grind-stage.duration-seconds");
    }

    public boolean isOreGenerationEnhancementEnabled() {
        return config.getBoolean("grind-stage.enhancements.ore-generation-enhancement.enabled");
    }

    public double getOreGenerationEnhancementReplacementChance() {
        return config.getDouble("grind-stage.enhancements.ore-generation-enhancement.replacement-chance");
    }

    public Set<Material> getOreGenerationEnhancementReplacements(Material material) {
        return oreGenerationEnhancementReplacements.getOrDefault(material, Set.of());
    }

    public boolean isOreLootIncreaseEnabled() {
        return config.getBoolean("grind-stage.enhancements.ore-loot-increase.enabled");
    }

    public int getOreLootIncreaseMinModifier() {
        return config.getInt("grind-stage.enhancements.ore-loot-increase.min-modifier");
    }

    public int getOreLootIncreaseMaxModifier() {
        return config.getInt("grind-stage.enhancements.ore-loot-increase.max-modifier");
    }

    public boolean isMobLootIncreaseEnabled() {
        return config.getBoolean("grind-stage.enhancements.mob-loot-increase.enabled");
    }

    public int getMobLootIncreaseMinModifier() {
        return config.getInt("grind-stage.enhancements.mob-loot-increase.min-modifier");
    }

    public int getMobLootIncreaseMaxModifier() {
        return config.getInt("grind-stage.enhancements.mob-loot-increase.max-modifier");
    }

    public boolean isSmeltingAccelerationEnabled() {
        return config.getBoolean("grind-stage.enhancements.smelting-acceleration.enabled");
    }

    public double getSmeltingAccelerationSpeedModifier() {
        return config.getDouble("grind-stage.enhancements.smelting-acceleration.speed-modifier");
    }

    public boolean isBlastFurnaceUpgradeEnabled() {
        return config.getBoolean("grind-stage.enhancements.blast-furnace-upgrade.enabled");
    }

    public boolean isSmokerUpgradeEnabled() {
        return config.getBoolean("grind-stage.enhancements.smoker-upgrade.enabled");
    }

    public boolean isCraftingTableEnchantmentEnabled() {
        return config.getBoolean("grind-stage.enhancements.crafting-table-enchantment.enabled");
    }

    public int getCraftingTableEnchantmentLevel(String tier) {
        return config.getInt("grind-stage.enhancements.crafting-table-enchantment.levels." + tier);
    }

    public boolean isAppleChanceIncreaseEnabled() {
        return config.getBoolean("grind-stage.enhancements.apple-chance-increase.enabled");
    }

    public int getAppleChanceIncreaseChanceModifier() {
        return config.getInt("grind-stage.enhancements.apple-chance-increase.chance-modifier");
    }

    public int getFightStageBorderNarrowingDurationSeconds() {
        return config.getInt("fight-stage.border-narrowing-duration-seconds");
    }

    private Map<Material, Set<Material>> loadOreGenerationEnhancementReplacements() {
        Map<Material, Set<Material>> replacements = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("grind-stage.enhancements.ore-generation-enhancement.replacements");

        if (section == null) {
            logger().warning("No ore-generation-enhancement replacements configuration found.");
            return replacements;
        }

        for (String rawKey : section.getKeys(false)) {
            Material key = Material.matchMaterial(rawKey.replace("-", "_"));
            if (key == null) {
                logger().warning("Invalid base material in ore replacement entry: " + rawKey);
                continue;
            }

            Set<Material> materials = new HashSet<>();
            for (String rawTarget : section.getStringList(rawKey)) {
                Material target = Material.matchMaterial(rawTarget);
                if (target == null) {
                    logger().warning("Invalid replacement target for " + key + ": " + rawTarget);
                    continue;
                }
                materials.add(target);
            }

            if (materials.isEmpty()) {
                logger().warning("No valid replacement targets for " + key + ", skipping.");
                continue;
            }

            replacements.put(key, materials);
        }

        return replacements;
    }
}
