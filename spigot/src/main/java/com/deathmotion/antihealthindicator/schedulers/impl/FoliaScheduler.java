package com.deathmotion.antihealthindicator.schedulers.impl;

import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class FoliaScheduler implements Scheduler<BukkitTask> {
    private final JavaPlugin plugin;
    private final GlobalRegionScheduler globalRegionScheduler;
    private final RegionScheduler regionScheduler;

    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.globalRegionScheduler = Bukkit.getServer().getGlobalRegionScheduler();
        this.regionScheduler = Bukkit.getServer().getRegionScheduler();
    }

    public BukkitTask runAsyncTask(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }
}