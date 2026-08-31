package com.plummy.outlastzone.core.workers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import static com.plummy.outlastzone.OutlastZone.getInstance;

public abstract class AbstractScheduledWorker implements ScheduledWorker {

    private BukkitTask task = null;

    protected abstract long getPeriodTicks();

    @Override
    public void start() {
        stop();
        task = Bukkit.getScheduler().runTaskTimer(getInstance(), this::execute, 20, getPeriodTicks());
    }

    @Override
    public void stop() {
        if (task != null) task.cancel();
        task = null;
    }

    @Override
    public boolean isRunning() {
        return task != null;
    }
}
