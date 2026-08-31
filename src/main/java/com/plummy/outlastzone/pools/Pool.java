package com.plummy.outlastzone.pools;

import com.plummy.outlastzone.core.Keyed;

public interface Pool<K extends Keyed<?>, V> {

    void push(K item, V value);

    V pop(K item);

    int size(K item);
}
