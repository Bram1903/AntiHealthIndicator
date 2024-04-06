package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.AHIPlugin;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import io.github.retrooper.packetevents.util.folia.FoliaCompatUtil;
import io.github.retrooper.packetevents.util.folia.TaskWrapper;
import org.bukkit.entity.Entity;

import java.util.concurrent.TimeUnit;

public final class SpigotScheduler implements Scheduler<TaskWrapper> {

    private final AHIPlugin plugin;

    public SpigotScheduler(AHIPlugin plugin) {
        this.plugin = plugin;
    }

    public TaskWrapper runTaskLater(Runnable runnable, long delay) {
        return FoliaCompatUtil.getGlobalRegionScheduler().runDelayed(plugin, (o) -> runnable.run(), delay);
    }

    public TaskWrapper runTaskTimer(Runnable runnable, long delay, long period) {
        return FoliaCompatUtil.getGlobalRegionScheduler().runAtFixedRate(plugin, (o) -> runnable.run(), delay, period);
    }

    @Override
    public TaskWrapper runTask(Runnable runnable) {
        return FoliaCompatUtil.getGlobalRegionScheduler().run(plugin, (o) -> runnable.run());
    }

    @Override
    public TaskWrapper runAsyncTask(Runnable runnable) {
        return FoliaCompatUtil.getAsyncScheduler().runNow(plugin, (o) -> runnable.run());
    }

    @Override
    public TaskWrapper runAsyncTaskLater(Object entity, Runnable runnable, long delay) {
        return FoliaCompatUtil.getEntityScheduler().run((Entity) entity, plugin, (o) -> runnable.run(), null);
    }

    @Override
    public TaskWrapper runAsyncTaskTimer(Runnable runnable, long delay, long period) {
        return FoliaCompatUtil.getAsyncScheduler().runAtFixedRate(plugin, (o) -> runnable.run(), delay, period, TimeUnit.HOURS);
    }

}