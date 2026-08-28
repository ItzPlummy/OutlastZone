package com.plummy.outlastzone.pools;

import com.plummy.outlastzone.terrain.Terrain;
import org.bukkit.Location;

import java.util.Collection;

import static com.plummy.outlastzone.OutlastZone.getTerrains;

public class LocationPool extends AbstractPool<Terrain, Location> {

    @Override
    protected Collection<Terrain> getAllItems() {
        return getTerrains().getAllTerrains();
    }
}
