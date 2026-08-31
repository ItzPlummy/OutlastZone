package com.plummy.outlastzone.core.games.phases;

import com.plummy.outlastzone.core.workers.AbstractPhaseWorker;

public class SetupPhaseWorker extends AbstractPhaseWorker {

    private static final long PERIOD_TICKS = 1L;
    private static final int REVEAL_TICK = 40;
    private static final int COMPLETION_TICK = 190;

    private final Runnable onReveal;
    private final Runnable onComplete;

    public SetupPhaseWorker(Runnable onReveal, Runnable onComplete) {
        this.onReveal = onReveal;
        this.onComplete = onComplete;
    }

    @Override
    protected long getPeriodTicks() {
        return PERIOD_TICKS;
    }

    @Override
    protected void onStep(int elapsed) {
        if (elapsed == REVEAL_TICK) {
            onReveal.run();
        }

        if (elapsed >= COMPLETION_TICK) {
            stop();
            onComplete.run();
        }
    }
}
