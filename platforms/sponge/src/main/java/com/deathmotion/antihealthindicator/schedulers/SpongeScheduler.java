package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SpongeScheduler implements Scheduler {
    private final Task.Builder taskBuilder;
    private final PluginContainer pluginContainer;
    private final org.spongepowered.api.scheduler.Scheduler asyncScheduler;

    public SpongeScheduler(PluginContainer pluginContainer) {
        this.taskBuilder = Task.builder();
        this.pluginContainer = pluginContainer;
        this.asyncScheduler = Sponge.asyncScheduler();
    }

    @Override
    public void runAsyncTask(Consumer<Object> task) {
        Task internalTask = taskBuilder
                .execute(task::accept)
                .plugin(pluginContainer)
                .build();

        this.asyncScheduler.submit(internalTask);
    }

    @Override
    public void runAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        Task internalTask = taskBuilder
                .execute(task::accept)
                .plugin(pluginContainer)
                .delay(delay, timeUnit)
                .build();

        this.asyncScheduler.submit(internalTask);
    }

    @Override
    public void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
        Task internalTask = taskBuilder
                .execute(task::accept)
                .plugin(pluginContainer)
                .interval(period, timeUnit)
                .delay(delay, timeUnit)
                .build();

        this.asyncScheduler.submit(internalTask);
    }
}
