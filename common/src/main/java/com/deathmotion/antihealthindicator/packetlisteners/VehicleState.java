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

package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.RidableEntities;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.data.cache.CachedEntity;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Listens for VehicleState events and manages the caching of various entity state details.
 *
 * @param <P> The platform type.
 */
public class VehicleState<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final ConfigManager<P> configManager;
    private final CacheManager<P> cacheManager;

    public VehicleState(AHIPlatform<P> platform) {
        this.platform = platform;
        this.configManager = platform.getConfigManager();
        this.cacheManager = platform.getCacheManager();

        platform.getLogManager().debug("Vehicle State listener has been set up.");
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        final Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled()) return;
        if (settings.getEntityData().isPlayersOnly()) return;

        final PacketTypeCommon type = event.getPacketType();

        if (PacketType.Play.Server.SET_PASSENGERS == type) {
            handlePassengers(new WrapperPlayServerSetPassengers(event), event.getUser(), settings);
        } else if (PacketType.Play.Server.ATTACH_ENTITY == type) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event), event.getUser(), settings);
        }
    }

    private void handlePassengers(WrapperPlayServerSetPassengers packet, User user, Settings settings) {
        int entityId = packet.getEntityId();
        if (entityId == user.getEntityId() || !isValidVehicle(user.getUUID(), entityId)) return;

        int[] passengers = packet.getPassengers();
        if (passengers.length > 0) {
            updatePassengerState(user, entityId, passengers[0], true, settings);
        } else {
            int passengerId = cacheManager.getPassengerId(user.getUUID(), entityId);
            updatePassengerState(user, entityId, passengerId, false, settings);
        }
    }

    private void handleAttachEntity(WrapperPlayServerAttachEntity packet, User user, Settings settings) {
        int entityId = packet.getHoldingId();
        if (entityId == user.getEntityId() || !isValidVehicle(user.getUUID(), entityId)) return;

        int passengerId = packet.getAttachedId();
        if (entityId > 0) {
            updatePassengerState(user, entityId, passengerId, true, settings);
        } else {
            int reversedEntityId = cacheManager.getEntityIdByPassengerId(user.getUUID(), passengerId);
            updatePassengerState(user, reversedEntityId, passengerId, false, settings);
        }
    }

    private void updatePassengerState(User user, int vehicleId, int passengerId, boolean entering, Settings settings) {
        cacheManager.updateVehiclePassenger(user.getUUID(), vehicleId, entering ? passengerId : -1);
        if (entering || user.getEntityId() == passengerId) {
            float healthValue = entering ? cacheManager.getVehicleHealth(user.getUUID(), vehicleId) : 0.5F;
            sendVehicleHealthUpdate(user, vehicleId, healthValue, entering, settings);
        }
    }

    private boolean isValidVehicle(UUID userUUID, int entityId) {
        return cacheManager.getCachedEntity(userUUID, entityId)
                .map(CachedEntity::getEntityType)
                .map(RidableEntities::isRideable)
                .orElse(false);
    }

    private void sendVehicleHealthUpdate(User user, int vehicleId, float healthValue, boolean entering, Settings settings) {
        platform.getScheduler().runAsyncTask((o) -> {
            if (!entering && settings.isAllowBypass() && platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass")) {
                return;
            }

            List<EntityData> metadata = Collections.singletonList(
                    new EntityData(
                            new MetadataIndex(user.getClientVersion()).HEALTH,
                            EntityDataTypes.FLOAT,
                            healthValue
                    )
            );

            user.sendPacketSilently(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
        });
    }
}