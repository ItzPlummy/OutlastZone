package com.plummy.outlastzone.core.data;

import com.plummy.outlastzone.core.Keyed;

public class AbstractRepository<K, V extends Keyed<K>> extends AbstractCatalog<K, V> implements Repository<K, V> {

    @Override
    public void add(V value) {
        super.add(value);
    }

    @Override
    public void remove(K key) {
        super.remove(key);
    }

    @Override
    public void clear() {
        super.clear();
    }
}
