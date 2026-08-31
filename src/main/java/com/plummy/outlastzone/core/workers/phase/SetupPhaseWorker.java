package com.plummy.outlastzone.core.workers.phase;

import com.plummy.outlastzone.core.workers.AbstractPhaseWorker;

public class SetupPhaseWorker extends AbstractPhaseWorker {

    private final Runnable onReveal;
    private final Runnable onComplete;

    public SetupPhaseWorker(Runnable onReveal, Runnable onComplete) {
        this.onReveal = onReveal;
        this.onComplete = onComplete;
    }

    @Override
    protected long getPeriodTicks() {
        return 1;
    }

    @Override
    protected void onStep(int elapsed) {
        if (elapsed == 40) {
            onReveal.run();
        }

        if (elapsed >= 190) {
            stop();
            onComplete.run();
        }
    }
}
