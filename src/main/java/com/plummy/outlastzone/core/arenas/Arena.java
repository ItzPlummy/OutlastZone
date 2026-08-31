package com.plummy.outlastzone.core.arenas;

import org.bukkit.Location;
import org.bukkit.World;

public interface Arena {

    Location getSpawnLocation();

    void prepareForSetup();

    void closeBorder(int playerCount);

    void shrinkBorder(int playerCount, int durationTicks);

    Location scatterPoint(int playerCount);

    default World getWorld() {
        return getSpawnLocation().getWorld();
    }
}
