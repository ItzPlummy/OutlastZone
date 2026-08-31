package com.plummy.outlastzone.core.data;

import com.plummy.outlastzone.core.keyed.Keyed;

public interface Repository<K, V extends Keyed<K>> extends Catalog<K, V> {

    void add(V value);

    void remove(K key);

    void clear();
}
