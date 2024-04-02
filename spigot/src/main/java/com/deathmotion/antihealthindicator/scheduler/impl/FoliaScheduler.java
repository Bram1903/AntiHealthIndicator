package com.deathmotion.antihealthindicator.scheduler.impl;


public class FoliaScheduler {
    public Object runTask(Runnable task) {
        GlobalScheduler.runTask(task);
        return null;
    }

    public Object runTaskAsynchronously(Runnable task) {
        return null;
    }

    public Object runTaskLater(Runnable task, long delay) {
        return null;
    }

    public Object runTaskTimer(Runnable task, long delay, long period) {
        return null;
    }

    public Object runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        return null;
    }
}