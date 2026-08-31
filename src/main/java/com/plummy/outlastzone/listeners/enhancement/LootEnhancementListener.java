package com.plummy.outlastzone.listeners.enhancement;

import com.destroystokyo.paper.MaterialTags;
import com.plummy.outlastzone.core.games.Game;
import com.plummy.outlastzone.core.games.GamePhase;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.getGameManager;
import static com.plummy.outlastzone.OutlastZone.getSettings;

public class LootEnhancementListener implements Listener {

    private static final Set<Material> ORES = MaterialTags.ORES.getValues();

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (!getSettings().isOreLootIncreaseEnabled()) return;
        if (!ORES.contains(event.getBlockState().getType())) return;

        Game game = getGameManager().getGame();

        if (game == null || game.getPhase() != GamePhase.GRINDING) {
            event.getItems().clear();
            return;
        }

        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) return;

        double minModifier = getSettings().getOreLootIncreaseMinModifier();
        double maxModifier = getSettings().getOreLootIncreaseMaxModifier();

        enhanceItemLoot(event.getItems(), minModifier, maxModifier);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!getSettings().isMobLootIncreaseEnabled()) return;
        if (!(event.getEntity() instanceof Lootable lootable)) return;

        Game game = getGameManager().getGame();

        if (game == null || game.getPhase() != GamePhase.GRINDING) {
            event.getDrops().clear();
            return;
        }

        int minModifier = getSettings().getMobLootIncreaseMinModifier();
        int maxModifier = getSettings().getMobLootIncreaseMaxModifier();

        enhanceLootableEntityLoot(event.getEntity(), lootable, event.getDrops(), minModifier, maxModifier);
    }

    private void enhanceItemLoot(List<Item> items, double minModifier, double maxModifier) {
        double modifier = minModifier + ThreadLocalRandom.current().nextDouble() * (maxModifier - minModifier);

        for (Item item : items) {
            ItemStack itemStack = item.getItemStack();
            int totalAmount = Math.toIntExact(Math.round(itemStack.getAmount() * modifier));

            List<ItemStack> stacks = splitItems(itemStack, totalAmount);

            item.setItemStack(stacks.getFirst());

            for (int i = 1; i < stacks.size(); i++) {
                Item newItem = item.getWorld().dropItem(item.getLocation(), stacks.get(i));
                newItem.setVelocity(item.getVelocity());
            }
        }
    }

    private void enhanceLootableEntityLoot(LivingEntity entity, Lootable lootable, List<ItemStack> items, int minModifier, int maxModifier) {
        LootTable lootTable = lootable.getLootTable();
        if (lootTable == null) return;

        LootContext context = new LootContext.Builder(entity.getLocation())
                .lootedEntity(entity)
                .killer(entity.getKiller())
                .build();

        int additionalRolls = ThreadLocalRandom.current().nextInt(minModifier - 1, maxModifier);

        for (int i = 0; i < additionalRolls; i++) {
            Collection<ItemStack> rolled = lootTable.populateLoot(ThreadLocalRandom.current(), context);
            items.addAll(rolled);
        }
    }

    private List<ItemStack> splitItems(ItemStack base, int totalAmount) {
        int maxStackSize = base.getMaxStackSize();
        List<ItemStack> stacks = new ArrayList<>();

        while (totalAmount > 0) {
            int amount = Math.min(totalAmount, maxStackSize);
            ItemStack stack = base.clone();
            stack.setAmount(amount);
            stacks.add(stack);
            totalAmount -= amount;
        }

        return stacks;
    }
}
