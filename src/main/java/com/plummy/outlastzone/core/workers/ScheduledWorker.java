package com.plummy.outlastzone.core.workers;

public interface ScheduledWorker extends Worker {

    void start();

    void stop();

    boolean isRunning();
}
