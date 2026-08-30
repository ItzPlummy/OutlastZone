package com.plummy.outlastzone.workers.spawnLocation;

public enum SpawnLocationWorkerPhase {

    IDLE("idle"),
    SEARCHING_FOR_ORIGIN("searchingForOrigin"),
    SEARCHING_FOR_STRUCTURE("searchingForStructure"),
    SELECTING_OPTIMAL_LOCATION("selectingOptimalLocation");

    private final String name;

    SpawnLocationWorkerPhase(String name) {
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
