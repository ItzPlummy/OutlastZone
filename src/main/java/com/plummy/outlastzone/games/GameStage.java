package com.plummy.outlastzone.games;

public enum GameStage {

    IDLE("idle"),
    LOCATING("locating"),
    SETUP("setup"),
    GRINDING("grinding"),
    FIGHTING("fighting"),
    FINISHED("finished");

    private final String name;

    GameStage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }
}
