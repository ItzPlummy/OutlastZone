package com.plummy.outlastzone.core.games.phases;

import com.plummy.outlastzone.core.data.repositories.ActivePlayerRepository;
import com.plummy.outlastzone.core.visual.bossBars.BossBarDisplay;
import com.plummy.outlastzone.core.visual.bossBars.FightBossBar;
import com.plummy.outlastzone.core.workers.AbstractPhaseWorker;

public class FightPhaseWorker extends AbstractPhaseWorker {

    private static final long PERIOD_TICKS = 20L;

    private final ActivePlayerRepository players;
    private final BossBarDisplay bossBar;

    public FightPhaseWorker(ActivePlayerRepository players) {
        this.players = players;
        this.bossBar = new FightBossBar();
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
    }
}
