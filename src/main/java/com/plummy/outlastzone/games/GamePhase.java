package com.plummy.outlastzone.games;

public enum GamePhase {

    IDLE("idle"),
    LOCATING("locating"),
    SETUP("setup"),
    GRINDING("grinding"),
    FIGHTING("fighting"),
    FINISHED("finished");

    private final String name;

    GamePhase(String name) {
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
