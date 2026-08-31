package com.plummy.outlastzone.core.workers;

public abstract class AbstractPhaseWorker extends AbstractScheduledWorker implements PhaseWorker {

    private int elapsed = 0;

    protected abstract void onStep(int elapsed);

    @Override
    public void start() {
        elapsed = 0;
        super.start();
    }

    @Override
    public void execute() {
        elapsed++;
        onStep(elapsed);
    }

    @Override
    public int getElapsed() {
        return elapsed;
    }
}
