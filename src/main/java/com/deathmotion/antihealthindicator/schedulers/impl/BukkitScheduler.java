package com.deathmotion.antihealthindicator.schedulers.impl;

import com.deathmotion.antihealthindicator.schedulers.ServerScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class BukkitScheduler implements ServerScheduler {

    BukkitTask bukkitTask;
    JavaPlugin plugin;

    public BukkitScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ServerScheduler runTask(Location location, Runnable task) {
        bukkitTask = Bukkit.getScheduler().runTask(plugin, task);
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskAsynchronously(Runnable task) {
        bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskLater(Location location, long delay, Runnable task) {
        bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskLaterAsynchronously(long delay, Runnable task) {
        bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskTimer(Location location, long delay, long period, Runnable task) {
        bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskTimerAsynchronously(long delay, long period, Runnable task) {
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
        return this;
    }

    @Override
    public void cancel() {
        if (!bukkitTask.isCancelled()) {
            bukkitTask.cancel();
        }
    }

}