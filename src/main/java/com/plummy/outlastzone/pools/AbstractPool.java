package com.plummy.outlastzone.pools;

import com.plummy.outlastzone.core.Keyed;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPool<K extends Keyed<?>, V> implements Pool<K, V> {

    private final Map<K, ArrayDeque<V>> pools = new HashMap<>();

    @Override
    public void push(K item, V value) {
        pools.computeIfAbsent(item, key -> new ArrayDeque<>()).push(value);
    }

    @Override
    public V pop(K item) {
        return pools.computeIfAbsent(item, key -> new ArrayDeque<>()).poll();
    }

    @Override
    public int size(K item) {
        return pools.getOrDefault(item, new ArrayDeque<>()).size();
    }
}
