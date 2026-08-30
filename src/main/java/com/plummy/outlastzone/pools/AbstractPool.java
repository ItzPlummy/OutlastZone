package com.plummy.outlastzone.pools;

import com.plummy.outlastzone.core.Keyed;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPool<T extends Keyed, V> implements Pool<T, V> {

    private final Map<T, ArrayDeque<V>> pools = new HashMap<>();

    @Override
    public void push(T item, V value) {
        pools.computeIfAbsent(item, key -> new ArrayDeque<>()).push(value);
    }

    @Override
    public V pop(T item) {
        return pools.computeIfAbsent(item, key -> new ArrayDeque<>()).poll();
    }

    @Override
    public int size(T item) {
        return pools.getOrDefault(item, new ArrayDeque<>()).size();
    }
}
