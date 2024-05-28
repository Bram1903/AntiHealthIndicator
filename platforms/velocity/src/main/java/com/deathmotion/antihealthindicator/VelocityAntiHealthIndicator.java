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

import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.retrooper.packetevents.bstats.Metrics;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.UUID;

public class VelocityAntiHealthIndicator extends AHIPlatform<ProxyServer> {

    private final ProxyServer proxy;
    private final Path dataDirectory;
    private final Logger logger;

    private ConfigManager configManager;

    @Inject
    public VelocityAntiHealthIndicator(ProxyServer proxy, @DataDirectory Path dataDirectory, Logger logger) {
        this.proxy = proxy;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    @Override
    public ProxyServer getPlatform() {
        return this.proxy;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    protected void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        Player player = this.proxy.getPlayer(sender).orElse(null);
        if (player == null) return false;

        return player.hasPermission(permission);
    }

    @Override
    public void broadcastComponent(Component component, @Nullable String permission) {
        this.proxy.getAllPlayers().stream()
                .filter(player -> permission == null || player.hasPermission(permission))
                .forEach(player -> player.sendMessage(component));
    }

    @Override
    public boolean isEntityRemoved(int entityId, @Nullable Object player) {
        return false;
    }

    @Override
    public boolean getConfigurationOption(ConfigOption option) {
        return this.configManager.getConfigurationOption(option);
    }

    @Override
    public String getPluginVersion() {
        return this.getPlatform().getVersion().getVersion();
    }

    protected void enableBStats() {
        try {
            Metrics.createInstance(this, this.getPlatform(), logger, dataDirectory, 20803);
        } catch (Exception e) {
            this.logger.warn("Something went wrong while enabling bStats.\n{}", e.getMessage());
        }
    }
}
