package com.plummy.outlastzone.visual;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;

public interface Displayable<T extends Display> {

    String getName();

    Material getItem();

    T spawn(Location location);
}
