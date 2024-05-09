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

import com.deathmotion.antihealthindicator.AHIPlugin;
import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SpigotScheduler implements Scheduler {

    private final AHIPlugin plugin;

    public SpigotScheduler(AHIPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsyncTask(Consumer<Object> task) {
        FoliaScheduler.getAsyncScheduler().runNow(plugin, task);
    }

    @Override
    public void rynAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        FoliaScheduler.getAsyncScheduler().runDelayed(plugin, task, delay, timeUnit);
    }

    @Override
    public void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
        FoliaScheduler.getAsyncScheduler().runAtFixedRate(plugin, task, delay, period, timeUnit);
    }
}