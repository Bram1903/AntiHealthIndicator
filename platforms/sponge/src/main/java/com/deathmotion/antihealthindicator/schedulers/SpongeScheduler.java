/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
