package com.plummy.outlastzone.visual.displays;

import com.plummy.outlastzone.terrain.Terrain;
import org.bukkit.entity.BlockDisplay;

import java.util.List;

import static com.plummy.outlastzone.OutlastZone.getTerrains;

public class TerrainDisplayWheel extends AbstractDisplayWheel<Terrain, BlockDisplay> {

    @Override
    public List<Terrain> getAvailableDisplays() {
        return getTerrains().all().stream().toList();
    }

    @Override
    public String getRevealMessage(Terrain item) {
        return "§eTerrain: " + item.getName();
    }
}
