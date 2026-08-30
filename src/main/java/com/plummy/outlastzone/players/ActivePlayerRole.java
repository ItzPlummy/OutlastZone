package com.plummy.outlastzone.players;

public enum ActivePlayerRole {

    PLAYER("player"),
    SPECTATOR("spectator");

    private final String name;

    ActivePlayerRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
