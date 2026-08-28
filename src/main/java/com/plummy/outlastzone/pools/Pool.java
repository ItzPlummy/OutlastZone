package com.plummy.outlastzone.pools;

import com.plummy.outlastzone.core.Keyed;

public interface Pool<T extends Keyed, V> {

    void push(T item, V value);

    V pop(T item);

    boolean contains(T item);

    int size(T item);
}
