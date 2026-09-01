package com.plummy.outlastzone.core.workers.phase;

import com.plummy.outlastzone.core.data.repositories.ActivePlayerRepository;
import com.plummy.outlastzone.core.visual.progressBars.ProgressBar;
import com.plummy.outlastzone.core.visual.progressBars.FightProgressBar;
import com.plummy.outlastzone.core.workers.AbstractPhaseWorker;

public class FightPhaseWorker extends AbstractPhaseWorker {

    private final ActivePlayerRepository players;
    private final ProgressBar bossBar;

    public FightPhaseWorker(ActivePlayerRepository players) {
        this.players = players;
        this.bossBar = new FightProgressBar();
    }

    public ProgressBar getBossBar() {
        return bossBar;
    }

    @Override
    protected long getPeriodTicks() {
        return 20;
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
    }
}
