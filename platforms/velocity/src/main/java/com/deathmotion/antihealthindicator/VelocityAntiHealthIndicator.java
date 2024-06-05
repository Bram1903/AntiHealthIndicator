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
import com.deathmotion.antihealthindicator.managers.VelocityConfigManager;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;
import java.util.UUID;

public class VelocityAntiHealthIndicator extends AHIPlatform<ProxyServer> {

    private final ProxyServer proxy;
    private final Path dataDirectory;
    private VelocityConfigManager velocityConfigManager;

    @Inject
    public VelocityAntiHealthIndicator(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.dataDirectory = dataDirectory;
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

    protected void setConfigManager(VelocityConfigManager velocityConfigManager) {
        this.velocityConfigManager = velocityConfigManager;
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        Player player = this.proxy.getPlayer(sender).orElse(null);
        if (player == null) return false;

        return player.hasPermission(permission);
    }

    @Override
    public boolean getConfigurationOption(ConfigOption option) {
        return this.velocityConfigManager.getConfigurationOption(option);
    }

    @Override
    public String getPluginDirectory() {
        return this.dataDirectory.toAbsolutePath().toString();
    }
}
