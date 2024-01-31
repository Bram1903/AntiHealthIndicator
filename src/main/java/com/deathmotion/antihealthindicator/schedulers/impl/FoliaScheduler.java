package com.deathmotion.antihealthindicator.schedulers.impl;

import com.deathmotion.antihealthindicator.schedulers.ServerScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class FoliaScheduler implements ServerScheduler {
    private static final long NANOSECONDS_PER_TICK = 50000000L;

    private final JavaPlugin plugin;
    private ScheduledTask scheduledTask;

    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ServerScheduler runTask(Location location, Runnable task) {
        if (location != null) {
            scheduledTask = plugin.getServer().getRegionScheduler().run(plugin, location, scheduledTask1 -> task.run());
        } else {
            scheduledTask = plugin.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask1 -> task.run());
        }
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskAsynchronously(Runnable task) {
        scheduledTask = plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask1 -> task.run());
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskLater(Location location, long delay, Runnable task) {
        if (location != null) {
            scheduledTask = plugin.getServer().getRegionScheduler().runDelayed(plugin, location, scheduledTask1 -> task.run(), delay);
        } else {
            scheduledTask = plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask1 -> task.run(), delay);
        }
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskLaterAsynchronously(long delay, Runnable task) {
        scheduledTask = plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask1 -> task.run(), delay * NANOSECONDS_PER_TICK, TimeUnit.NANOSECONDS);
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskTimer(Location location, long delay, long period, Runnable task) {
        scheduledTask = plugin.getServer().getRegionScheduler().runAtFixedRate(plugin, location, scheduledTask1 -> task.run(), delay, period);
        return this;
    }

    @Override
    public @NotNull ServerScheduler runTaskTimerAsynchronously(long delay, long period, Runnable task) {
        scheduledTask = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, scheduledTask1 -> task.run(), delay * NANOSECONDS_PER_TICK, period * NANOSECONDS_PER_TICK, TimeUnit.NANOSECONDS);
        return this;
    }

    @Override
    public void cancel() {
        if (!scheduledTask.isCancelled()) {
            scheduledTask.cancel();
        }
    }

}