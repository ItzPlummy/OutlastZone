package com.plummy.outlastzone.workers;

public interface ScheduledWorker extends Worker {

    void start();

    void stop();

    boolean isRunning();
}
