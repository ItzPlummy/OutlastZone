package com.plummy.outlastzone.pools;

import com.plummy.outlastzone.core.Keyed;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPool<T extends Keyed, V> implements Pool<T, V> {

    private final Map<T, ArrayDeque<V>> pools;

    public AbstractPool() {
        this.pools = new HashMap<>();
        getAllItems().forEach(item -> pools.put(item, new ArrayDeque<>()));
    }

    protected abstract Collection<T> getAllItems();

    @Override
    public void push(T item, V value) {
        if (contains(item)) pools.get(item).push(value);
    }

    @Override
    public V pop(T item) {
        return contains(item) ? pools.get(item).pop() : null;
    }

    @Override
    public boolean contains(T item) {
        return pools.containsKey(item);
    }

    @Override
    public int size(T item) {
        return contains(item) ? pools.get(item).size() : -1;
    }
}
