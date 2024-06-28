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
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.managers.CacheManager;
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

/**
 * Listens for VehicleState events and manages the caching of various entity state details.
 *
 * @param <P> The platform type.
 */
public class VehicleState<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final Settings settings;
    private final CacheManager<P> cacheManager;

    /**
     * Constructs a new VehicleState with the specified {@link AHIPlatform}.
     *
     * @param platform The platform to use.
     */
    public VehicleState(AHIPlatform<P> platform) {
        this.platform = platform;
        this.settings = platform.getConfigManager().getSettings();
        this.cacheManager = platform.getCacheManager();

        platform.getLogManager().debug("Vehicle State listener has been set up.");
    }

    /**
     * This function is called when an {@link PacketSendEvent} is triggered.
     * Manages the state of various entities based on the event triggered.
     *
     * @param event The event that has been triggered.
     */
    @Override
    public void onPacketSend(PacketSendEvent event) {
        final PacketTypeCommon type = event.getPacketType();

        if (PacketType.Play.Server.SET_PASSENGERS == type) {
            handleSetPassengers(new WrapperPlayServerSetPassengers(event), event.getUser());
        } else if (PacketType.Play.Server.ATTACH_ENTITY == type) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event), event.getUser());
        }
    }

    private void handleSetPassengers(WrapperPlayServerSetPassengers packet, User user) {
        int entityId = packet.getEntityId();
        if (entityId == user.getEntityId()) return;

        int[] passengers = packet.getPassengers();

        if (passengers.length > 0) {
            cacheManager.updateVehiclePassenger(user.getUUID(), entityId, passengers[0]);
            handlePassengerEvent(user, entityId, cacheManager.getVehicleHealth(user.getUUID(), entityId), true);
        } else {
            int passengerId = cacheManager.getPassengerId(user.getUUID(), entityId);
            cacheManager.updateVehiclePassenger(user.getUUID(), entityId, -1);

            if (user.getEntityId() == passengerId) {
                handlePassengerEvent(user, entityId, 0.5F, false);
            }
        }
    }

    private void handleAttachEntity(WrapperPlayServerAttachEntity packet, User user) {
        int entityId = packet.getHoldingId();
        if (entityId == user.getEntityId()) return;

        int passengerId = packet.getAttachedId();

        if (entityId > 0) {
            cacheManager.updateVehiclePassenger(user.getUUID(), entityId, passengerId);
            handlePassengerEvent(user, entityId, cacheManager.getVehicleHealth(user.getUUID(), entityId), true);
        } else {
            // With the Entity Attach packet, the entity ID is set to -1 when the entity is detached;
            // Thus we need to retrieve the vehicle we stepped of by using a reverse lookup by passenger ID
            int reversedEntityId = cacheManager.getEntityIdByPassengerId(user.getUUID(), passengerId);
            cacheManager.updateVehiclePassenger(user.getUUID(), reversedEntityId, -1);

            if (user.getEntityId() == passengerId) {
                handlePassengerEvent(user, reversedEntityId, 0.5F, false);
            }
        }
    }

    private void handlePassengerEvent(User user, int vehicleId, float healthValue, boolean entering) {
        platform.getScheduler().runAsyncTask((o) -> {
            if (!entering && settings.isAllowBypass()) {
                if (platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass")) return;
            }

            List<EntityData> metadata = Collections.singletonList(new EntityData(new MetadataIndex(user.getClientVersion()).HEALTH, EntityDataTypes.FLOAT, healthValue));
            user.sendPacketSilently(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
        });
    }
}