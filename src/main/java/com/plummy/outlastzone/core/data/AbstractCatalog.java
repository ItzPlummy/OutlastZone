package com.plummy.outlastzone.core.data;

import com.plummy.outlastzone.core.keyed.Keyed;

import java.util.*;

public abstract class AbstractCatalog<K, V extends Keyed<K>> implements Catalog<K, V> {

    private final Map<K, V> map = new HashMap<>();

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public Collection<V> all() {
        return Collections.unmodifiableCollection(map.values());
    }

    @Override
    public boolean has(K key) {
        return map.containsKey(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    protected void add(V value) {
        map.put(value.getKey(), value);
    }

    protected void remove(K key) {
        map.remove(key);
    }

    protected void clear() {
        map.clear();
    }
}
