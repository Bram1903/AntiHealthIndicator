package com.deathmotion.antihealthindicator.schedulers.impl;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaScheduler {
    private final JavaPlugin plugin;
    private final GlobalRegionScheduler globalRegionScheduler;
    private final RegionScheduler regionScheduler;

    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.globalRegionScheduler = Bukkit.getServer().getGlobalRegionScheduler();
        this.regionScheduler = Bukkit.getServer().getRegionScheduler();
    }

    public Object runAsyncTask(Runnable runnable) {
        //
    }
}