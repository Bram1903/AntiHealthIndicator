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
import com.deathmotion.antihealthindicator.cache.entities.CachedEntity;
import com.deathmotion.antihealthindicator.cache.entities.RidableEntities;
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
import java.util.List;

/**
 * Listens for VehicleState events and manages the caching of various entity state details.
 */
public class VehicleTracker {
    private final AHIPlayer player;
    private final EntityCache entityCache;
    private final ConfigManager<?> configManager;

    public VehicleTracker(AHIPlayer player, EntityCache entityCache) {
        this.player = player;
        this.entityCache = entityCache;
        this.configManager = AHIPlatform.getInstance().getConfigManager();
    }

    public void onPacketSend(PacketSendEvent event) {
        final Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled()) return;
        if (settings.getEntityData().isPlayersOnly()) return;

        final PacketTypeCommon type = event.getPacketType();

        if (PacketType.Play.Server.SET_PASSENGERS == type) {
            handlePassengers(new WrapperPlayServerSetPassengers(event));
        } else if (PacketType.Play.Server.ATTACH_ENTITY == type) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event));
        }
    }

    private void handlePassengers(WrapperPlayServerSetPassengers packet) {
        int entityId = packet.getEntityId();
        if (entityId == player.user.getEntityId() || !isValidVehicle(entityId)) return;

        int[] passengers = packet.getPassengers();
        if (passengers.length > 0) {
            updatePassengerState(entityId, passengers[0], true);
        } else {
            int passengerId = entityCache.getPassengerId(entityId);
            updatePassengerState(entityId, passengerId, false);
        }
    }

    private void handleAttachEntity(WrapperPlayServerAttachEntity packet) {
        int entityId = packet.getHoldingId();
        if (entityId == player.user.getEntityId() || !isValidVehicle(entityId)) return;

        int passengerId = packet.getAttachedId();
        if (entityId > 0) {
            updatePassengerState(entityId, passengerId, true);
        } else {
            int reversedEntityId = entityCache.getEntityIdByPassengerId(passengerId);
            updatePassengerState(reversedEntityId, passengerId, false);
        }
    }

    private void updatePassengerState(int vehicleId, int passengerId, boolean entering) {
        entityCache.updateVehiclePassenger(vehicleId, entering ? passengerId : -1);
        if (entering || player.user.getEntityId() == passengerId) {
            float healthValue = entering ? entityCache.getVehicleHealth(vehicleId) : 0.5F;
            sendVehicleHealthUpdate(vehicleId, healthValue);
        }
    }

    private boolean isValidVehicle(int entityId) {
        return entityCache.getCachedEntity(entityId)
                .map(CachedEntity::getEntityType)
                .map(RidableEntities::isRideable)
                .orElse(false);
    }

    private void sendVehicleHealthUpdate(int vehicleId, float healthValue) {
        AHIPlatform.getInstance().getScheduler().runAsyncTask((o) -> {
            List<EntityData> metadata = Collections.singletonList(
                    new EntityData(
                            player.metadataIndex.HEALTH,
                            EntityDataTypes.FLOAT,
                            healthValue
                    )
            );

            player.user.sendPacketSilently(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
        });
    }
}