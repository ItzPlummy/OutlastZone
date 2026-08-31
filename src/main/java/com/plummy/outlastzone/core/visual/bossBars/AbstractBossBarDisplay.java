package com.plummy.outlastzone.core.visual.bossBars;

import com.plummy.outlastzone.core.players.BasePlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public abstract class AbstractBossBarDisplay implements BossBarDisplay {

    private final BossBar bossBar;

    public AbstractBossBarDisplay(BossBar.Color color, BossBar.Overlay overlay) {
        this.bossBar = BossBar.bossBar(Component.empty(), 0, color, overlay);
    }

    protected abstract String getName(int elapsed);

    protected abstract float getProgress(int elapsed);

    @Override
    public void update(int elapsed) {
        bossBar.name(Component.text(getName(elapsed)));
        bossBar.progress(Math.clamp(getProgress(elapsed), 0f, 1f));
    }

    @Override
    public void showTo(BasePlayer player) {
        if (player.isOnline()) player.getBukkitPlayer().showBossBar(bossBar);
    }

    @Override
    public void hideFrom(BasePlayer player) {
        if (player.isOnline()) player.getBukkitPlayer().hideBossBar(bossBar);
    }

    protected String formatTime(int seconds) {
        int total = Math.max(0, seconds);
        return String.format("%02d:%02d", total / 60, total % 60);
    }
}
