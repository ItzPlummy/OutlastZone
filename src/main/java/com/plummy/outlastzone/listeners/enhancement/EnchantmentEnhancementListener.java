package com.plummy.outlastzone.listeners.enhancement;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.plummy.outlastzone.OutlastZone.getInstance;
import static com.plummy.outlastzone.OutlastZone.getNamespacedKey;

public class EnchantmentEnhancementListener implements Listener {

    private static final Set<Material> ENCHANTABLE_ITEMS = Arrays.stream(Material.values())
            .filter(Material::isItem)
            .filter(
                    material -> {
                        try {
                            ItemStack testStack = new ItemStack(material);
                            if (testStack.isEmpty()) return false;

                            ItemStack enchanted = testStack.enchantWithLevels(30, false, ThreadLocalRandom.current());
                            return !enchanted.getEnchantments().isEmpty();
                        } catch (Exception exception) {
                            return false;
                        }
                    }
                    )
            .collect(Collectors.toSet());

    public static final Map<Material, String> TIERS = Map.<Material, String>ofEntries(
            Map.entry(Material.WOODEN_SWORD, "wooden"),
            Map.entry(Material.WOODEN_PICKAXE, "wooden"),
            Map.entry(Material.WOODEN_AXE, "wooden"),
            Map.entry(Material.WOODEN_SHOVEL, "wooden"),
            Map.entry(Material.WOODEN_HOE, "wooden"),
            Map.entry(Material.WOODEN_SPEAR, "wooden"),

            Map.entry(Material.STONE_SWORD, "stone"),
            Map.entry(Material.STONE_PICKAXE, "stone"),
            Map.entry(Material.STONE_AXE, "stone"),
            Map.entry(Material.STONE_SHOVEL, "stone"),
            Map.entry(Material.STONE_HOE, "stone"),
            Map.entry(Material.STONE_SPEAR, "stone"),

            Map.entry(Material.COPPER_SWORD, "copper"),
            Map.entry(Material.COPPER_PICKAXE, "copper"),
            Map.entry(Material.COPPER_AXE, "copper"),
            Map.entry(Material.COPPER_SHOVEL, "copper"),
            Map.entry(Material.COPPER_HOE, "copper"),
            Map.entry(Material.COPPER_SPEAR, "copper"),
            Map.entry(Material.COPPER_HELMET, "copper"),
            Map.entry(Material.COPPER_CHESTPLATE, "copper"),
            Map.entry(Material.COPPER_LEGGINGS, "copper"),
            Map.entry(Material.COPPER_BOOTS, "copper"),

            Map.entry(Material.LEATHER_HELMET, "leather"),
            Map.entry(Material.LEATHER_CHESTPLATE, "leather"),
            Map.entry(Material.LEATHER_LEGGINGS, "leather"),
            Map.entry(Material.LEATHER_BOOTS, "leather"),

            Map.entry(Material.CHAINMAIL_HELMET, "chainmail"),
            Map.entry(Material.CHAINMAIL_CHESTPLATE, "chainmail"),
            Map.entry(Material.CHAINMAIL_LEGGINGS, "chainmail"),
            Map.entry(Material.CHAINMAIL_BOOTS, "chainmail"),

            Map.entry(Material.IRON_SWORD, "iron"),
            Map.entry(Material.IRON_PICKAXE, "iron"),
            Map.entry(Material.IRON_AXE, "iron"),
            Map.entry(Material.IRON_SHOVEL, "iron"),
            Map.entry(Material.IRON_HOE, "iron"),
            Map.entry(Material.IRON_SPEAR, "iron"),
            Map.entry(Material.IRON_HELMET, "iron"),
            Map.entry(Material.IRON_CHESTPLATE, "iron"),
            Map.entry(Material.IRON_LEGGINGS, "iron"),
            Map.entry(Material.IRON_BOOTS, "iron"),

            Map.entry(Material.GOLDEN_SWORD, "golden"),
            Map.entry(Material.GOLDEN_PICKAXE, "golden"),
            Map.entry(Material.GOLDEN_AXE, "golden"),
            Map.entry(Material.GOLDEN_SHOVEL, "golden"),
            Map.entry(Material.GOLDEN_HOE, "golden"),
            Map.entry(Material.GOLDEN_SPEAR, "golden"),
            Map.entry(Material.GOLDEN_HELMET, "golden"),
            Map.entry(Material.GOLDEN_CHESTPLATE, "golden"),
            Map.entry(Material.GOLDEN_LEGGINGS, "golden"),
            Map.entry(Material.GOLDEN_BOOTS, "golden"),

            Map.entry(Material.DIAMOND_SWORD, "diamond"),
            Map.entry(Material.DIAMOND_PICKAXE, "diamond"),
            Map.entry(Material.DIAMOND_AXE, "diamond"),
            Map.entry(Material.DIAMOND_SHOVEL, "diamond"),
            Map.entry(Material.DIAMOND_HOE, "diamond"),
            Map.entry(Material.DIAMOND_SPEAR, "diamond"),
            Map.entry(Material.DIAMOND_HELMET, "diamond"),
            Map.entry(Material.DIAMOND_CHESTPLATE, "diamond"),
            Map.entry(Material.DIAMOND_LEGGINGS, "diamond"),
            Map.entry(Material.DIAMOND_BOOTS, "diamond"),

            Map.entry(Material.NETHERITE_SWORD, "netherite"),
            Map.entry(Material.NETHERITE_PICKAXE, "netherite"),
            Map.entry(Material.NETHERITE_AXE, "netherite"),
            Map.entry(Material.NETHERITE_SHOVEL, "netherite"),
            Map.entry(Material.NETHERITE_HOE, "netherite"),
            Map.entry(Material.NETHERITE_SPEAR, "netherite"),
            Map.entry(Material.NETHERITE_HELMET, "netherite"),
            Map.entry(Material.NETHERITE_CHESTPLATE, "netherite"),
            Map.entry(Material.NETHERITE_LEGGINGS, "netherite"),
            Map.entry(Material.NETHERITE_BOOTS, "netherite")
    );

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof Keyed keyed) || !keyed.getKey().equals(getNamespacedKey())) return;

        if (!getInstance().getConfig().getBoolean("grind-phase.enhancements.crafting-table-enchantment.enable", true)) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }

        ItemStack center = event.getInventory().getMatrix()[4];
        if (center == null || center.getType() == Material.AIR) return;
        if (!(event.getView().getPlayer() instanceof Player player)) return;

        Random random = new Random(player.getEnchantmentSeed());
        String tier = TIERS.getOrDefault(center.getType(), "default");
        int level = getInstance().getConfig().getInt("grind-phase.enhancements.crafting-table-enchantment.levels." + tier);

        ItemStack result = center.clone();
        result.setAmount(1);
        result = result.enchantWithLevels(level, false, random);

        event.getInventory().setResult(result);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof Keyed keyed) || !keyed.getKey().equals(getNamespacedKey())) return;

        if (event.getWhoClicked() instanceof Player player) {
            player.setEnchantmentSeed(ThreadLocalRandom.current().nextInt());
        }
    }

    public static void registerRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getNamespacedKey(), new ItemStack(Material.KNOWLEDGE_BOOK));

        recipe.shape("LLL", "LIL", "LLL");
        recipe.setIngredient('L', new RecipeChoice.MaterialChoice(Material.LAPIS_LAZULI));
        recipe.setIngredient('I', new RecipeChoice.MaterialChoice(new ArrayList<>(ENCHANTABLE_ITEMS)));

        Bukkit.addRecipe(recipe);
    }
}
