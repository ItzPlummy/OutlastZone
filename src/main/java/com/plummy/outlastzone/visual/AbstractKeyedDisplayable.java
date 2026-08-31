package com.plummy.outlastzone.visual;

import com.plummy.outlastzone.core.AbstractKeyed;
import org.bukkit.Material;
import org.bukkit.entity.Display;

public abstract class AbstractKeyedDisplayable<K, T extends Display> extends AbstractKeyed<K> implements Displayable<T> {

    private final String name;
    private final Material item;

    public AbstractKeyedDisplayable(K key, String name, Material item) {
        super(key);
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
