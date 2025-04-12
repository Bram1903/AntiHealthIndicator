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
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.RidableEntities;
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
        Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled() || settings.getEntityData().isPlayersOnly()) {
            return;
        }

        PacketTypeCommon type = event.getPacketType();
        if (PacketType.Play.Server.SET_PASSENGERS == type) {
            handlePassengers(new WrapperPlayServerSetPassengers(event));
        } else if (PacketType.Play.Server.ATTACH_ENTITY == type) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event));
        }
    }

    /**
     * Processes SET_PASSENGERS packets.
     */
    private void handlePassengers(WrapperPlayServerSetPassengers packet) {
        int vehicleId = packet.getEntityId();
        if (!shouldProcess(vehicleId)) {
            return;
        }

        int[] passengers = packet.getPassengers();
        if (passengers.length > 0) {
            // If there are passengers, update with the first one.
            updatePassengerState(vehicleId, passengers[0], true);
        } else {
            // If no passengers are present, consider the vehicle empty.
            int currentPassenger = entityCache.getPassengerId(vehicleId);
            updatePassengerState(vehicleId, currentPassenger, false);
        }
    }

    /**
     * Processes ATTACH_ENTITY packets.
     */
    private void handleAttachEntity(WrapperPlayServerAttachEntity packet) {
        int vehicleId = packet.getHoldingId();
        if (!shouldProcess(vehicleId)) {
            return;
        }

        int passengerId = packet.getAttachedId();
        if (vehicleId > 0) {
            updatePassengerState(vehicleId, passengerId, true);
        } else {
            int cachedVehicleId = entityCache.getEntityIdByPassengerId(passengerId);
            updatePassengerState(cachedVehicleId, passengerId, false);
        }
    }

    /**
     * Updates the passenger state in the cache and, when applicable, sends a vehicle health update.
     *
     * @param vehicleId   the id of the vehicle entity.
     * @param passengerId the id of the passenger.
     * @param entering    true if the passenger is entering, false if leaving.
     */
    private void updatePassengerState(int vehicleId, int passengerId, boolean entering) {
        // Update the cached passenger state (use -1 for leaving).
        entityCache.updateVehiclePassenger(vehicleId, entering ? passengerId : -1);
        // If a passenger is entering or the player's entity is involved, send a health update.
        if (entering || player.user.getEntityId() == passengerId) {
            float healthValue = entering ? entityCache.getVehicleHealth(vehicleId) : 0.5F;
            sendVehicleHealthUpdate(vehicleId, healthValue);
        }
    }

    /**
     * Determines whether a given entity id should be processed.
     *
     * @param entityId the entity id to check.
     * @return true if it is not the player and represents a valid rideable vehicle.
     */
    private boolean shouldProcess(int entityId) {
        return entityId != player.user.getEntityId() && isValidVehicle(entityId);
    }

    /**
     * Checks if the entity is a valid rideable vehicle.
     *
     * @param entityId the entity id.
     * @return true if the entity is present in the cache and is rideable.
     */
    private boolean isValidVehicle(int entityId) {
        return entityCache.getCachedEntity(entityId)
                .map(CachedEntity::getEntityType)
                .map(RidableEntities::isRideable)
                .orElse(false);
    }

    /**
     * Sends an asynchronous vehicle health update to the client.
     *
     * @param vehicleId   the vehicle entity id.
     * @param healthValue the health value.
     */
    private void sendVehicleHealthUpdate(final int vehicleId, final float healthValue) {
        AHIPlatform.getInstance().getScheduler().runAsyncTask(task -> {
            List<EntityData<?>> metadata = Collections.singletonList(
                    new EntityData<>(
                            player.metadataIndex.HEALTH,
                            EntityDataTypes.FLOAT,
                            healthValue
                    )
            );
            player.user.sendPacketSilently(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
        });
    }
}
