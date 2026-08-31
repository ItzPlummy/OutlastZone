package com.plummy.outlastzone.listeners.enhancement;

import com.plummy.outlastzone.core.games.Game;
import com.plummy.outlastzone.core.games.GamePhase;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.plummy.outlastzone.OutlastZone.*;

public class AppleEnhancementListener implements Listener {

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        Game game = getGameManager().getGame();

        if (game == null || game.getPhase() != GamePhase.GRINDING) {
            event.getBlock().setType(Material.AIR);
            event.setCancelled(true);
            return;
        }

        if (!getSettings().isAppleDropIncreaseEnabled()) return;

        event.setCancelled(true);

        Collection<ItemStack> drops = event.getBlock().getDrops().stream().filter(itemStack -> itemStack.getType() != Material.APPLE).toList();

        for (ItemStack drop : drops) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
        }

        dropApples(event.getBlock().getState());

        event.getBlock().setType(Material.AIR);
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (!List.of(Material.OAK_LEAVES, Material.DARK_OAK_LEAVES).contains(event.getBlockState().getType())) return;

        Game game = getGameManager().getGame();

        if (game == null || game.getPhase() != GamePhase.GRINDING) {
            event.getItems().clear();
            return;
        }

        if (!getSettings().isAppleDropIncreaseEnabled()) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0) return;

        event.getItems().removeIf(item -> item.getItemStack().getType() == Material.APPLE);
        dropApples(event.getBlockState());
    }

    private void dropApples(BlockState block) {
        int minModifier = getSettings().getAppleDropIncreaseMinModifier();
        int maxModifier = getSettings().getAppleDropIncreaseMaxModifier();

        for (int index = 0; index < ThreadLocalRandom.current().nextInt(minModifier, maxModifier + 1); index++) {
            Collection<ItemStack> appleDrops = block.getDrops().stream().filter(itemStack -> itemStack.getType() == Material.APPLE).toList();

            for (ItemStack drop : appleDrops) {
                block.getWorld().dropItemNaturally(block.getLocation(), drop);
            }
        }
    }
}
