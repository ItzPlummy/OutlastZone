package com.plummy.outlastzone.core.workers;

public abstract class AbstractWorker implements Worker {

    private final double intervalTicks;
    private double tick = 0;

    public AbstractWorker() {
        this(1);
    }

    public AbstractWorker(double intervalTicks) {
        this.intervalTicks = intervalTicks;
    }

    public void step() {
        tick += 1;

        while (tick >= intervalTicks) {
            tick -= intervalTicks;
            execute();
        }
    }
}
