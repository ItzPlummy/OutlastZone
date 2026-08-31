package com.plummy.outlastzone.visual.displays;

import org.bukkit.Material;
import org.bukkit.entity.Display;

public abstract class AbstractDisplayable<T extends Display> implements Displayable<T> {

    private final String name;
    private final Material item;

    public AbstractDisplayable(String name, Material item) {
        this.name = name;
        this.item = item;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Material getItem() {
        return item;
    }
}
