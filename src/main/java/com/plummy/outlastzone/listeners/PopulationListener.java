package com.plummy.outlastzone.listeners;

import com.plummy.outlastzone.populators.PopulatorRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class PopulationListener implements Listener {

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        event.getWorld().getPopulators().add(PopulatorRegistry.ORE_ENHANCEMENT_POPULATOR);
    }
}
