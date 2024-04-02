package com.deathmotion.antihealthindicator.scheduler;

import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;

public class SchedulerAdapter implements Scheduler {

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Object runTask(Runnable task) {
        return null;
    }

    @Override
    public Object runTaskAsynchronously(Runnable task) {
        return null;
    }

    @Override
    public Object runTaskLater(Runnable task, long delay) {
        return null;
    }

    @Override
    public Object runTaskTimer(Runnable task, long delay, long period) {
        return null;
    }

    @Override
    public Object runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        return null;
    }
}