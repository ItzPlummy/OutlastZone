package com.plummy.outlastzone.games;

public enum GameFinishReason {

    PLAYER_OUTLASTED("player_outlasted"),
    STOP_COMMAND_EXECUTED("stop_command_executed"),
    NO_LOCATION_FOUND("no_location_found");

    private final String name;

    GameFinishReason(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }
}
