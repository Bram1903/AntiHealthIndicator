package com.deathmotion.antihealthindicator.scheduler;

import com.deathmotion.antihealthindicator.scheduler.impl.FoliaScheduler;
import com.deathmotion.antihealthindicator.scheduler.impl.SpigotAdapter;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;

public class SchedulerAdapter {
    public Scheduler getScheduler() {
        if (isFolia()) {
            return new FoliaScheduler();
        } else {
            return new SpigotAdapter();
        }
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}