package com.plummy.outlastzone.listeners.enhancement;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static com.plummy.outlastzone.OutlastZone.getInstance;

public class SmeltingEnhancementListener implements Listener {

    @EventHandler
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        if (!getInstance().getConfig().getBoolean("grind-phase.enhancements.smelting-acceleration.enable", true)) return;

        event.setTotalCookTime((int) (event.getTotalCookTime() * getInstance().getConfig().getDouble("grind-phase.enhancements.smelting-acceleration.speed-modifier", 0.25)));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.getPlayer().isSneaking()) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.FURNACE) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        Material targetType;

        if (item.getType() == Material.IRON_INGOT && getInstance().getConfig().getBoolean("grind-phase.enhancements.blast-furnace-upgrade.enable", true)) {
            targetType = Material.BLAST_FURNACE;
        } else if (Tag.ITEMS_LOGS.isTagged(item.getType()) && getInstance().getConfig().getBoolean("grind-phase.enhancements.smoker-upgrade.enable", true)) {
            targetType = Material.SMOKER;
        } else {
            return;
        }

        event.setCancelled(true);

        upgradeFurnace(block, targetType);
        item.setAmount(item.getAmount() - 1);
    }

    private void upgradeFurnace(Block block, Material targetType) {
        Furnace oldState = (Furnace) block.getState();
        BlockData oldData = oldState.getBlockData();

        BlockFace facing = ((Directional) oldData).getFacing();
        boolean wasLit = oldData instanceof Lightable oldLightable && oldLightable.isLit();
        ItemStack[] contents = oldState.getInventory().getContents().clone();
        short burnTime = oldState.getBurnTime();
        short cookTime = oldState.getCookTime();

        block.getWorld().spawnParticle(Particle.BLOCK, block.getLocation().add(0.5, 0.5, 0.5), 40, 0.3, 0.3, 0.3, oldData);
        block.getWorld().playSound(block.getLocation(), oldData.getSoundGroup().getBreakSound(), 1f, 0.8f);

        block.setType(targetType, false);

        Furnace blockDataState = (Furnace) block.getState();
        BlockData newData = blockDataState.getBlockData();

        ((Directional) newData).setFacing(facing);
        if (newData instanceof Lightable newLightable) newLightable.setLit(wasLit);

        blockDataState.setBlockData(newData);
        blockDataState.update(true, false);

        Furnace inventoryState = (Furnace) block.getState();
        inventoryState.getInventory().setContents(contents);

        short newCookTimeTotal = 100;

        if (getInstance().getConfig().getBoolean("grind-phase.enhancements.smelting-acceleration.enable", true)) {
            newCookTimeTotal = (short) (newCookTimeTotal * getInstance().getConfig().getDouble("grind-phase.enhancements.smelting-acceleration.speed-modifier", 0.25));
        }

        Furnace cookState = (Furnace) block.getState();
        cookState.setBurnTime(burnTime);
        cookState.setCookTimeTotal(newCookTimeTotal);
        cookState.setCookTime((short) Math.min(cookTime, newCookTimeTotal));
        cookState.update(true, false);
    }
}
