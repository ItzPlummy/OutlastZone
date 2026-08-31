package com.plummy.outlastzone.games.arena;

import org.bukkit.Location;
import org.bukkit.World;

public interface Arena {

    Location getSpawnLocation();

    World getWorld();

    void prepareForSetup();

    void closeBorder(int playerCount);

    void shrinkBorder(int playerCount, int durationTicks);

    Location scatterPoint(int playerCount);
}
