package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.AHIPlugin;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public final class SpigotScheduler implements Scheduler<BukkitTask> {

    private final AHIPlugin plugin;

    public SpigotScheduler(AHIPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    @Override
    public BukkitTask runTaskTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    @Override
    public BukkitTask runTask(Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    @Override
    public BukkitTask runAsyncTask(Runnable runnable) {
        FoliaCompatUtil.runTaskAsync(plugin, runnable);
        return null; // PR to packet events so it returns BukkitTask
    }

    @Override
    public BukkitTask runAsyncTaskLater(Object entity, Runnable runnable, long delay) {
        if (entity == null) {
            return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
        else {
            FoliaCompatUtil.runTaskForEntity((Entity) entity, plugin, runnable, () -> {}, delay);
        }
        return null; // PR to packet events so it returns BukkitTask
    }

    @Override
    public BukkitTask runAsyncTaskTimer(Runnable runnable, long delay, long period) {
        FoliaCompatUtil.runTaskTimerAsync(plugin, (o) -> runnable.run(), delay, period);
        return null; // PR to packet events so it returns BukkitTask
    }

}