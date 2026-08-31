package com.plummy.outlastzone.games.phases;

import com.plummy.outlastzone.repositories.ActivePlayerRepository;
import com.plummy.outlastzone.visual.bossBars.BossBarDisplay;
import com.plummy.outlastzone.visual.bossBars.GrindBossBar;
import com.plummy.outlastzone.workers.AbstractPhaseWorker;

public class GrindPhaseWorker extends AbstractPhaseWorker {

    private static final long PERIOD_TICKS = 20L;

    private final ActivePlayerRepository players;
    private final BossBarDisplay bossBar;
    private final int durationSeconds;
    private final Runnable onComplete;

    public GrindPhaseWorker(ActivePlayerRepository players, int durationSeconds, Runnable onComplete) {
        this.players = players;
        this.bossBar = new GrindBossBar(durationSeconds);
        this.durationSeconds = durationSeconds;
        this.onComplete = onComplete;
    }

    public BossBarDisplay getBossBar() {
        return bossBar;
    }

    @Override
    protected long getPeriodTicks() {
        return PERIOD_TICKS;
    }

    @Override
    public void start() {
        super.start();

        bossBar.update(0);
        bossBar.showTo(players.allOnline());
    }

    @Override
    protected void onStep(int elapsed) {
        bossBar.update(elapsed);

        if (elapsed >= durationSeconds) {
            onComplete.run();
        }
    }
}
