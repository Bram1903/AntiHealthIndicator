package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.AHIBungee;
import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class BungeeScheduler implements Scheduler {

    private final AHIBungee plugin;

    public BungeeScheduler(AHIBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsyncTask(Consumer<Object> task) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> task.accept(null));
    }

    @Override
    public void runAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> task.accept(null), delay, timeUnit);
    }

    @Override
    public void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
        ProxyServer.getInstance().getScheduler().schedule(plugin, () -> task.accept(null), delay, period, timeUnit);
    }
}