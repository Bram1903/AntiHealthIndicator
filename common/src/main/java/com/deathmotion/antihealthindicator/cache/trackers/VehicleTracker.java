/*
 *  This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  Copyright (C) 2025 Bram and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.cache.trackers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.cache.EntityCache;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;

import java.util.Collections;

public class VehicleTracker {
    private final AHIPlayer player;
    private final EntityCache cache;
    private final ConfigManager<?> configManager;

    public VehicleTracker(AHIPlayer player, EntityCache cache) {
        this.player = player;
        this.cache = cache;
        this.configManager = AHIPlatform.getInstance().getConfigManager();
    }

    public void onPacketSend(PacketSendEvent event) {
        Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled() || settings.getEntityData().isPlayersOnly()) {
            return;
        }

        PacketTypeCommon type = event.getPacketType();
        if (type == PacketType.Play.Server.SET_PASSENGERS) {
            handleSetPassengers(new WrapperPlayServerSetPassengers(event));
        } else if (type == PacketType.Play.Server.ATTACH_ENTITY) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event));
        }
    }

    private void handleSetPassengers(WrapperPlayServerSetPassengers packet) {
        int vehicleId = packet.getEntityId();
        if (!shouldProcess(vehicleId)) return;

        int[] passengers = packet.getPassengers();
        if (passengers.length > 0) {
            // first passenger entering
            updateState(vehicleId, passengers[0], true);
        } else {
            // nobody aboard → exiting
            int old = cache.getPassengerId(vehicleId);
            updateState(vehicleId, old, false);
        }
    }

    private void handleAttachEntity(WrapperPlayServerAttachEntity packet) {
        int vehicleId = packet.getHoldingId();
        int passenger = packet.getAttachedId();

        // attaching to a vehicle (>0) is enter; holdingId==0 is detach
        if (vehicleId > 0) {
            if (!shouldProcess(vehicleId)) return;
            updateState(vehicleId, passenger, true);

        } else {
            // passenger leaving: find which vehicle they came from
            int realVehicle = cache.getEntityIdByPassengerId(passenger);
            if (!shouldProcess(realVehicle)) return;
            updateState(realVehicle, passenger, false);
        }
    }

    private void updateState(int vehicleId, int passengerId, boolean entering) {
        // 1 map lookup + update via computeIfPresent
        cache.updateVehiclePassenger(vehicleId, entering ? passengerId : -1);

        // Only send metadata if the tracked player is the one entering or exiting
        if (player.user.getEntityId() != passengerId) {
            return; // not our player → skip
        }

        float health = entering
                ? cache.getVehicleHealth(vehicleId)
                : 0.5F;
        sendMetadata(vehicleId, health);
    }

    private boolean shouldProcess(int entityId) {
        // skip self & non-rideables
        if (entityId == player.user.getEntityId()) return false;
        return cache.isRideableVehicle(entityId);
    }

    private void sendMetadata(final int vehicleId, final float health) {
        AHIPlatform.getInstance().getScheduler().runAsyncTask(task -> player.user.sendPacketSilently(
                new WrapperPlayServerEntityMetadata(
                        vehicleId,
                        Collections.singletonList(new EntityData<>(
                                player.metadataIndex.HEALTH,
                                EntityDataTypes.FLOAT,
                                health
                        ))
                )
        ));
    }
}
