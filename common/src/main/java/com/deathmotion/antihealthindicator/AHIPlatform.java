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
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.PacketManager;
import com.deathmotion.antihealthindicator.managers.UpdateManager;
import com.deathmotion.antihealthindicator.wrappers.PlatformLoggerWrapperImpl;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import com.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public abstract class AHIPlatform<P> {

    private final PlatformLoggerWrapperImpl loggerWrapper = new PlatformLoggerWrapperImpl();
    protected Scheduler scheduler;
    private CacheManager<P> cacheManager;

    public void commonOnLoad() {
        // Load common stuff
    }

    public void commonOnEnable() {
        cacheManager = new CacheManager<>(this);

        new UpdateManager<>(this);
        new PacketManager<>(this);
    }

    public void commonOnDisable() {
        PacketEvents.getAPI().terminate();
    }

    public abstract P getPlatform();

    public abstract boolean hasPermission(UUID sender, String permission);

    public abstract void broadcastComponent(Component component, @Nullable String permission);

    public abstract boolean isEntityRemoved(int entityId, @Nullable Object player);

    public abstract boolean getConfigurationOption(ConfigOption option);

    public abstract String getPluginVersion();
}