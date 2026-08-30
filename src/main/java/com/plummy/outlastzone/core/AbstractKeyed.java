package com.plummy.outlastzone.core;

public abstract class AbstractKeyed implements Keyed {

    private final String key;

    public AbstractKeyed(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Keyed keyed)) return false;

        return getKey().equals(keyed.getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
