package com.plummy.outlastzone.core.visual.bossBars;

import net.kyori.adventure.bossbar.BossBar;

public class FightBossBar extends AbstractBossBarDisplay {

    public FightBossBar() {
        super(BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    @Override
    protected String getName(int elapsed) {
        return "§c§lFight Phase§c | " + formatTime(elapsed);
    }

    @Override
    protected float getProgress(int elapsed) {
        return 1;
    }
}
