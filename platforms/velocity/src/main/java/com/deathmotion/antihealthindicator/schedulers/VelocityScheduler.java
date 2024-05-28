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

import com.deathmotion.antihealthindicator.AHIVelocity;
import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class VelocityScheduler implements Scheduler {

    private final AHIVelocity ahiVelocity;
    private final ProxyServer proxy;

    public VelocityScheduler(AHIVelocity ahiVelocity, ProxyServer server) {
        this.ahiVelocity = ahiVelocity;
        this.proxy = server;
    }

    @Override
    public void runAsyncTask(Consumer<Object> task) {
        this.proxy.getScheduler()
                .buildTask(this.ahiVelocity, () -> task.accept(null))
                .schedule();
    }

    @Override
    public void rynAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        this.proxy.getScheduler()
                .buildTask(this.ahiVelocity, () -> task.accept(null))
                .delay(delay, timeUnit)
                .schedule();
    }

    @Override
    public void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
        this.proxy.getScheduler()
                .buildTask(this.ahiVelocity, () -> task.accept(null))
                .delay(delay, timeUnit)
                .repeat(period, timeUnit)
                .schedule();
    }
}