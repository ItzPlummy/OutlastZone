package com.plummy.outlastzone.listeners.enhancement;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;

import static com.plummy.outlastzone.OutlastZone.getInstance;

public class FoodEnhancementListener implements Listener {

    @EventHandler
    public void onEntityExhaustion(EntityExhaustionEvent event) {
        if (!getInstance().getConfig().getBoolean("grind-stage.enhancements.exhaustion-accumulation.enable", true)) return;

        event.setExhaustion((float) (event.getExhaustion() * getInstance().getConfig().getDouble("grind-stage.enhancements.exhaustion-accumulation.speed-modifier")));
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {

    }
}
