package com.plummy.outlastzone.visual.bossBars;

import net.kyori.adventure.bossbar.BossBar;

public class GrindBossBar extends AbstractBossBarDisplay {

    private final int durationSeconds;

    public GrindBossBar(int durationSeconds) {
        super(BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        this.durationSeconds = durationSeconds;
    }

    @Override
    protected String getName(int elapsed) {
        return "§e§lGrind Phase§e | " + formatTime(durationSeconds - elapsed) + " left";
    }

    @Override
    protected float getProgress(int elapsed) {
        return (float) elapsed / durationSeconds;
    }
}
