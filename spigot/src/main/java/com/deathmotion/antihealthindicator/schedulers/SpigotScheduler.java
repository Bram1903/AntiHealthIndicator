package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.AHIPlugin;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import io.github.retrooper.packetevents.util.folia.FoliaCompatUtil;
import io.github.retrooper.packetevents.util.folia.TaskWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public final class SpigotScheduler implements Scheduler<TaskWrapper> {

    private final AHIPlugin plugin;

    public SpigotScheduler(AHIPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public TaskWrapper runTaskLater(Runnable runnable, long delay) {
        return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
    }

    @Override
    public TaskWrapper runTaskTimer(Runnable runnable, long delay, long period) {
        return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period));
    }

    @Override
    public TaskWrapper runTask(Runnable runnable) {
        return new TaskWrapper(Bukkit.getScheduler().runTask(plugin, runnable));
    }

    @Override
    public TaskWrapper runAsyncTask(Runnable runnable) {
        return FoliaCompatUtil.runTaskAsync(plugin, (o) -> runnable.run());
    }

    @Override
    public TaskWrapper runAsyncTaskLater(Object entity, Runnable runnable, long delay) {
        if (entity == null) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
        }
        else {
            return FoliaCompatUtil.runTaskForEntity((Entity) entity, plugin, runnable, () -> {}, delay);
        }
    }

    @Override
    public TaskWrapper runAsyncTaskTimer(Runnable runnable, long delay, long period) {
        return FoliaCompatUtil.runTaskTimerAsync(plugin, (o) -> runnable.run(), delay, period);
    }

}