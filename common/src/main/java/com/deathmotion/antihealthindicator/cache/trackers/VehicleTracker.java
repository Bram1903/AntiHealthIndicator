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
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.deathmotion.antihealthindicator.models.Settings;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import lombok.Getter;

import java.util.Collections;
import java.util.Optional;

public class VehicleTracker {

    private static final float MIN_SPOOF_HEALTH = 1F;

    private final AHIPlayer player;
    private final EntityCache cache;
    private final ConfigManager<?> configManager;

    public VehicleTracker(final AHIPlayer player, final EntityCache cache) {
        this.player = player;
        this.cache = cache;
        this.configManager = AHIPlatform.getInstance().getConfigManager();
    }

    public void onPacketSend(final PacketSendEvent event) {
        final Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled()) return;
        if (!settings.getEntityData().isHealth()) return;
        if (settings.getEntityData().isPlayersOnly()) return;

        final PacketTypeCommon type = event.getPacketType();
        if (type == PacketType.Play.Server.SET_PASSENGERS) {
            handleSetPassengers(new WrapperPlayServerSetPassengers(event));
        } else if (type == PacketType.Play.Server.ATTACH_ENTITY) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event));
        }
    }

    private void handleSetPassengers(final WrapperPlayServerSetPassengers packet) {
        final int vehicleId = packet.getEntityId();
        if (vehicleId == player.user.getEntityId()) return;

        boolean iAmPassenger = false;
        for (int id : packet.getPassengers()) {
            if (id == player.user.getEntityId()) {
                iAmPassenger = true;
                break;
            }
        }

        Optional<Integer> currentOpt = cache.getCurrentVehicleId();

        if (iAmPassenger) {
            if (currentOpt.map(id -> id != vehicleId).orElse(true)) {
                updateState(vehicleId, true);
            }
        } else {
            currentOpt.ifPresent(currentVehicleId -> {
                if (currentVehicleId == vehicleId) {
                    updateState(vehicleId, false);
                }
            });
        }
    }

    private void handleAttachEntity(final WrapperPlayServerAttachEntity packet) {
        final int vehicleId = packet.getHoldingId();
        final int passengerId = packet.getAttachedId();

        if (passengerId != player.user.getEntityId()) return;

        Optional<Integer> currentOpt = cache.getCurrentVehicleId();

        if (vehicleId > 0) {
            if (vehicleId != player.user.getEntityId() && currentOpt.map(id -> id != vehicleId).orElse(true)) {
                updateState(vehicleId, true);
            }
        } else {
            currentOpt.ifPresent(currentVehicleId -> {
                if (currentVehicleId != player.user.getEntityId()) {
                    updateState(currentVehicleId, false);
                }
            });
        }
    }

    private void updateState(final int vehicleId, final boolean entering) {
        final CachedEntity entity = cache.getEntity(vehicleId);
        if (entity == null) {
            if (!entering) {
                cache.setCurrentVehicleId(null);
            }
            return;
        }

        final Optional<Integer> currentOpt = cache.getCurrentVehicleId();

        if (entering) {
            if (currentOpt.isPresent() && currentOpt.get() == vehicleId) {
                return;
            }
            cache.setCurrentVehicleId(vehicleId);
        } else {
            if (currentOpt.map(id -> id != vehicleId).orElse(true)) {
                return;
            }
            cache.setCurrentVehicleId(null);
        }

        AHIPlatform.getInstance().getScheduler().runAsyncTask(task -> {
            PacketPair pair = createPackets(vehicleId, entity, entering);

            if (configManager.getSettings().getEntityData().isHealth()) {
                player.user.sendPacketSilently(pair.getAttributePacket());
                player.user.sendPacketSilently(pair.getMetadataPacket());
            }
        });
    }

    private PacketPair createPackets(int vehicleId, CachedEntity entity, boolean entering) {

        final float spoofedMaxHealth = entering
                ? Math.max(entity.getMaxHealth(), MIN_SPOOF_HEALTH)
                : 1.0f;

        WrapperPlayServerUpdateAttributes attributePacket =
                new WrapperPlayServerUpdateAttributes(
                        vehicleId,
                        Collections.singletonList(
                                new WrapperPlayServerUpdateAttributes.Property(
                                        Attributes.MAX_HEALTH,
                                        spoofedMaxHealth,
                                        Collections.emptyList()
                                )
                        )
                );

        final float spoofedHealth = entering
                ? Math.max(entity.getHealth(), MIN_SPOOF_HEALTH)
                : MIN_SPOOF_HEALTH;

        WrapperPlayServerEntityMetadata metadataPacket =
                new WrapperPlayServerEntityMetadata(
                        vehicleId,
                        Collections.singletonList(
                                new EntityData<>(
                                        player.metadataIndex.HEALTH,
                                        EntityDataTypes.FLOAT,
                                        spoofedHealth
                                )
                        )
                );

        return new PacketPair(attributePacket, metadataPacket);
    }

    @Getter
    private static final class PacketPair {
        private final WrapperPlayServerUpdateAttributes attributePacket;
        private final WrapperPlayServerEntityMetadata metadataPacket;

        public PacketPair(WrapperPlayServerUpdateAttributes attributePacket, WrapperPlayServerEntityMetadata metadataPacket) {
            this.attributePacket = attributePacket;
            this.metadataPacket = metadataPacket;
        }
    }
}
