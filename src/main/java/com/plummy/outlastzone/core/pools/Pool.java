package com.plummy.outlastzone.core.pools;

import com.plummy.outlastzone.core.keyed.Keyed;

public interface Pool<K extends Keyed<?>, V> {

    void push(K item, V value);

    V pop(K item);

    int size(K item);
}
