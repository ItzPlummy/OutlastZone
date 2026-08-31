package com.plummy.outlastzone.core.keyed;

public abstract class AbstractKeyed<K> implements Keyed<K> {

    private final K key;

    public AbstractKeyed(K key) {
        this.key = key;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Keyed<?> keyed)) return false;
        return getKey().equals(keyed.getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
