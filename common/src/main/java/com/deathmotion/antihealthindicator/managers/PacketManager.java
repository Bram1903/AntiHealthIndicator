/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2025 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
import com.deathmotion.antihealthindicator.packetlisteners.EntityTracker;
import com.deathmotion.antihealthindicator.packetlisteners.VehicleState;
import com.deathmotion.antihealthindicator.packetlisteners.spoofers.*;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;

/**
 * Manager for handling packet listeners
 *
 * @param <P> The platform type
 */
public class PacketManager<P> {
    private final AHIPlatform<P> platform;

    /**
     * Constructs a new PacketManager with the specified {@link AHIPlatform}.
     *
     * @param platform The platform to use.
     */
    public PacketManager(AHIPlatform<P> platform) {
        this.platform = platform;

        setupPacketListeners();
        platform.getLogManager().debug("Packet listeners have been set up.");
    }

    /**
     * Sets up packet listeners
     */
    public void setupPacketListeners() {
        PacketEvents.getAPI().getEventManager().registerListener(new EntityTracker<>(platform), PacketListenerPriority.LOW);
        PacketEvents.getAPI().getEventManager().registerListener(new EntityMetadataListener<>(platform));
        PacketEvents.getAPI().getEventManager().registerListener(new VehicleState<>(platform));
        PacketEvents.getAPI().getEventManager().registerListener(new EntityEquipmentListener<>(platform));
        PacketEvents.getAPI().getEventManager().registerListener(new PlayerUpdateHealthListener<>(platform));
        PacketEvents.getAPI().getEventManager().registerListener(new WorldSeedListener<>(platform));
        PacketEvents.getAPI().getEventManager().registerListener(new ScoreboardListener<>(platform));
    }
}