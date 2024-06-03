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
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.LogManager;
import com.deathmotion.antihealthindicator.managers.PacketManager;
import com.deathmotion.antihealthindicator.managers.UpdateManager;
import com.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public abstract class AHIPlatform<P> {
    protected Scheduler scheduler;
    private LogManager<P> logManager;
    private CacheManager<P> cacheManager;

    /**
     * Called when the platform is enabled.
     */
    public void commonOnEnable() {
        logManager = new LogManager<>(this);
        cacheManager = new CacheManager<>(this);

        new UpdateManager<>(this);
        new PacketManager<>(this);
    }

    /**
     * Called when the platform gets disabled.
     */
    public void commonOnDisable() {
        PacketEvents.getAPI().terminate();
    }

    /**
     * Sends a broadcast message with a specific component and permission.
     *
     * @param component  The component to broadcast.
     * @param permission The permission required to receive the broadcast. Can be null.
     */
    public void broadcastComponent(Component component, @Nullable String permission) {
        PacketEvents.getAPI().getProtocolManager().getUsers().stream()
                .filter(user -> permission == null || hasPermission(user.getUUID(), permission))
                .forEach(user -> user.sendMessage(component));
    }

    /**
     * Gets the platform.
     *
     * @return The platform.
     */
    public abstract P getPlatform();

    /**
     * Checks if a sender has a certain permission.
     *
     * @param sender     The UUID of the entity to check.
     * @param permission The permission string to check.
     * @return true if the entity has the permission, false otherwise.
     */
    public abstract boolean hasPermission(UUID sender, String permission);

    /**
     * Retrieves the value of a configuration option.
     *
     * @param option The configuration option to retrieve.
     * @return The value of the configuration option.
     */
    public abstract boolean getConfigurationOption(ConfigOption option);
}