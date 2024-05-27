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
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VelocityAntiHealthIndicator extends AHIPlatform<ProxyServer> {

    private final ProxyServer proxy;

    public VelocityAntiHealthIndicator(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public ProxyServer getPlatform() {
        return this.proxy;
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
        return false;
    }

    @Override
    public String getPluginVersion() {
        return this.proxy.getVersion().toString();
    }
}
