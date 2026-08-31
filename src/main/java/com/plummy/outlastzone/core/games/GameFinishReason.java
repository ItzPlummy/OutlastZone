package com.plummy.outlastzone.core.games;

public enum GameFinishReason {

    PLAYER_OUTLASTED("playerOutlasted"),
    STOP_COMMAND_EXECUTED("stopCommandExecuted"),
    LOCATION_NOT_READY("locationNotReady"),;

    private final String name;

    GameFinishReason(String name) {
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
