package com.deathmotion.antihealthindicator.schedulers.impl;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class SpigotScheduler {
    private final JavaPlugin plugin;
    private final BukkitScheduler scheduler;

    public SpigotScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        scheduler = Bukkit.getScheduler();
    }

    public BukkitTask runAsyncTask(Runnable runnable) {
        return scheduler.runTaskAsynchronously(plugin, runnable);
    }
}