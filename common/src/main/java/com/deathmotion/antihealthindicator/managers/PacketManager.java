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

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.packetlisteners.EntityState;
import com.deathmotion.antihealthindicator.packetlisteners.spoofers.*;
import com.github.retrooper.packetevents.PacketEvents;

public class PacketManager<P> {
    private final AHIPlatform<P> platform;

    public PacketManager(AHIPlatform<P> platform) {
        this.platform = platform;

        setupPacketListeners();
    }

    public void setupPacketListeners() {
        setupEntityListeners();
        setupAdditionalListeners();

        PacketEvents.getAPI().init();
    }

    private void setupEntityListeners() {
        if (platform.getConfigurationOption(ConfigOption.ENTITY_DATA_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new EntityState<>(this.platform));
            PacketEvents.getAPI().getEventManager().registerListener(new EntityMetadataListener<>(this.platform));
        }
    }

    private void setupAdditionalListeners() {
        if (platform.getConfigurationOption(ConfigOption.ITEMS_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new EntityEquipmentListener<>(this.platform));
        }
        if (platform.getConfigurationOption(ConfigOption.SPOOF_FOOD_SATURATION_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new PlayerUpdateHealthListener<>(this.platform));
        }
        if (platform.getConfigurationOption(ConfigOption.SPOOF_WORLD_SEED_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new WorldSeedListener<>(this.platform));
        }
        if (platform.getConfigurationOption(ConfigOption.ENCHANTMENTS_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new WindowItemsListener<>(this.platform));
        }
    }
}