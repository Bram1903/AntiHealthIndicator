package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.schedulers.impl.SpigotScheduler;
import com.deathmotion.antihealthindicator.schedulers.impl.FoliaScheduler;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class SchedulerAbstract implements Scheduler {
    private final SpigotScheduler spigotScheduler;
    private final FoliaScheduler foliaScheduler;

    public SchedulerAbstract(JavaPlugin plugin) {
        this.spigotScheduler = new SpigotScheduler(plugin);
        this.foliaScheduler = new FoliaScheduler(plugin);
    }

    @Override
    public Object runAsyncTask(Runnable runnable) {
        if (isFolia()) {
            return foliaScheduler.runAsyncTask(runnable);
        } else {
            return spigotScheduler.runAsyncTask(runnable);
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