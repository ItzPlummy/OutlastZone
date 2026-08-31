package com.plummy.outlastzone.core.data;

import com.plummy.outlastzone.core.Keyed;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public interface Catalog<K, V extends Keyed<K>> {

    V get(K key);

    Collection<V> all();

    boolean has(K key);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    default V any() {
        return all().stream().findAny().orElse(null);
    }

    default V random() {
        if (isEmpty()) {
            return null;
        }

        return all().stream().skip(ThreadLocalRandom.current().nextInt(size())).findFirst().orElse(null);
    }
}
