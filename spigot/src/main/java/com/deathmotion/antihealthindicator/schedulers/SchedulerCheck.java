package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.AHIPlugin;
import com.deathmotion.antihealthindicator.schedulers.impl.SpigotScheduler;
import com.deathmotion.antihealthindicator.schedulers.impl.FoliaScheduler;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import org.bukkit.scheduler.BukkitTask;

public final class SchedulerCheck {

    public static Scheduler<BukkitTask> createNew(AHIPlugin plugin) {
        if (isFolia()) {
            return new FoliaScheduler(plugin);
        }
        else {
            return new SpigotScheduler(plugin);
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