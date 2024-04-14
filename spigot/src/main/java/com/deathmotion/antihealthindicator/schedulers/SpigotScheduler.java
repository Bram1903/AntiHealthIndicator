package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.AHIPlugin;
import com.deathmotion.antihealthindicator.schedulers.folia.FoliaCompatUtil;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SpigotScheduler implements Scheduler {

    private final AHIPlugin plugin;

    public SpigotScheduler(AHIPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsyncTask(Consumer<Object> task) {
        FoliaCompatUtil.getAsyncScheduler().runNow(plugin, task);
    }

    @Override
    public void rynAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        FoliaCompatUtil.getAsyncScheduler().runDelayed(plugin, task, delay, timeUnit);
    }
}