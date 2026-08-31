package com.plummy.outlastzone.core.workers.spawnLocation;

import org.bukkit.Location;
import org.bukkit.generator.structure.Structure;

public class StructureSearchContext {

    private Structure structure = null;
    private Location origin = null;
    private int x = 0;
    private int z = 0;

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean xIsWithinSearchRadius(int searchRadius) {
        return x <= getOrigin().getBlockX() + searchRadius;
    }

    public void resetX(int searchRadius) {
        setX(getOrigin().getBlockX() - searchRadius);
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean zIsWithinSearchRadius(int searchRadius) {
        return z <= getOrigin().getBlockZ() + searchRadius;
    }

    public void resetZ(int searchRadius) {
        setZ(getOrigin().getBlockZ() - searchRadius);
    }
}
