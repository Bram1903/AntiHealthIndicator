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

package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.commands.VelocityAHICommand;
import com.deathmotion.antihealthindicator.managers.VelocityConfigManager;
import com.deathmotion.antihealthindicator.schedulers.VelocityScheduler;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.retrooper.packetevents.bstats.Metrics;
import org.slf4j.Logger;

import java.nio.file.Path;

public class AHIVelocity {
    private final ProxyServer server;
    private final Path dataDirectory;
    private final Logger logger;
    private final VelocityAntiHealthIndicator ahi;

    @Inject
    public AHIVelocity(ProxyServer server, @DataDirectory Path dataDirectory, Logger logger) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.ahi = new VelocityAntiHealthIndicator(server);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignoredEvent) {
        ahi.setScheduler(new VelocityScheduler(this, this.server));
        ahi.setConfigManager(new VelocityConfigManager(this.logger, this.dataDirectory));

        ahi.commonOnEnable();

        registerCommands();
        enableBStats();
    }

    @Subscribe()
    public void onProxyShutdown(ProxyShutdownEvent ignoredEvent) {
        ahi.commonOnDisable();
    }

    private void enableBStats() {
        try {
            Metrics metrics = Metrics.createInstance(this, this.ahi.getPlatform(), logger, dataDirectory, 20803);
            metrics.addCustomChart(new Metrics.SimplePie("antihealthindicator_version", () -> AHIPlatform.class.getPackage().getImplementationVersion()));
            metrics.addCustomChart(new Metrics.SimplePie("antihealthindicator_platform", () -> "Velocity"));
        } catch (Exception e) {
            this.logger.warn("Something went wrong while enabling bStats.\n{}", e.getMessage());
        }
    }

    private void registerCommands() {
        new VelocityAHICommand(server);
    }
}