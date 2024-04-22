/*
 *
 *  *
 *  *  * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  *  * Copyright (C) 2024 Bram and contributors
 *  *  *
 *  *  * This program is free software: you can redistribute it and/or modify
 *  *  * it under the terms of the GNU General Public License as published by
 *  *  * the Free Software Foundation, either version 3 of the License, or
 *  *  * (at your option) any later version.
 *  *  *
 *  *  * This program is distributed in the hope that it will be useful,
 *  *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  * GNU General Public License for more details.
 *  *  *
 *  *  * You should have received a copy of the GNU General Public License
 *  *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  *
 *
 */

package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.RidableEntities;
import com.deathmotion.antihealthindicator.data.cache.LivingEntityData;
import com.deathmotion.antihealthindicator.data.cache.RidableEntityData;
import com.deathmotion.antihealthindicator.data.cache.WolfData;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EntityState<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final CacheManager cacheManager;

    private final boolean isBypassEnabled;

    public EntityState(AHIPlatform<P> platform) {
        this.platform = platform;
        this.cacheManager = platform.getCacheManager();

        this.isBypassEnabled = platform.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        final PacketTypeCommon type = event.getPacketType();

        if (PacketType.Play.Server.SPAWN_LIVING_ENTITY == type) {
            handleSpawnLivingEntity(new WrapperPlayServerSpawnLivingEntity(event));
            return;
        }

        if (PacketType.Play.Server.SPAWN_ENTITY == type) {
            handleSpawnEntity(new WrapperPlayServerSpawnEntity(event));
            return;
        }

        if (PacketType.Play.Server.JOIN_GAME == type) {
            handleJoinGame(new WrapperPlayServerJoinGame(event));
            return;
        }

        if (PacketType.Play.Server.ENTITY_METADATA == type) {
            handleEntityMetadata(new WrapperPlayServerEntityMetadata(event), event.getUser());
        }

        if (PacketType.Play.Server.SET_PASSENGERS == type) {
            handleSetPassengers(new WrapperPlayServerSetPassengers(event), event.getUser());
            return;
        }

        if (PacketType.Play.Server.ATTACH_ENTITY == type) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event), event.getUser());
            return;
        }

        if (PacketType.Play.Server.DESTROY_ENTITIES == type) {
            handleEntityDestroy(new WrapperPlayServerDestroyEntities(event), event.getPlayer());
        }
    }

    private void handleSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet) {
        int entityId = packet.getEntityId();
        EntityType entityType = packet.getEntityType();

        LivingEntityData entityData = createLivingEntity(entityType);
        this.cacheManager.addLivingEntity(entityId, entityData);
    }

    private void handleSpawnEntity(WrapperPlayServerSpawnEntity packet) {
        EntityType entityType = packet.getEntityType();

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.LIVINGENTITY)) {
            int entityId = packet.getEntityId();

            LivingEntityData entityData = createLivingEntity(entityType);
            this.cacheManager.addLivingEntity(entityId, entityData);
        }
    }

    private void handleJoinGame(WrapperPlayServerJoinGame packet) {
        LivingEntityData livingEntityData = new LivingEntityData();
        livingEntityData.setEntityType(EntityTypes.PLAYER);

        this.cacheManager.addLivingEntity(packet.getEntityId(), livingEntityData);
    }

    private void handleEntityMetadata(WrapperPlayServerEntityMetadata packet, User user) {
        int entityId = packet.getEntityId();

        LivingEntityData entityData = this.cacheManager.getLivingEntityData(entityId).orElse(null);
        if (entityData == null) return;

        packet.getEntityMetadata().forEach(metaData -> {
            entityData.processMetaData(metaData, user);
        });
    }

    private void handleSetPassengers(WrapperPlayServerSetPassengers packet, User user) {
        int entityId = packet.getEntityId();
        int[] passengers = packet.getPassengers();

        if (passengers.length > 0) {
            this.cacheManager.updateVehiclePassenger(entityId, passengers[0]);
            handlePassengerEvent(user, entityId, this.cacheManager.getVehicleHealth(entityId), true);
        } else {
            int passengerId = this.cacheManager.getPassengerId(entityId);
            this.cacheManager.updateVehiclePassenger(entityId, -1);

            if (user.getEntityId() == passengerId) {
                handlePassengerEvent(user, entityId, 0.5F, false);
            }
        }
    }

    private void handleAttachEntity(WrapperPlayServerAttachEntity packet, User user) {
        int entityId = packet.getHoldingId();
        int passengerId = packet.getAttachedId();

        if (entityId > 0) {
            this.cacheManager.updateVehiclePassenger(entityId, passengerId);
            handlePassengerEvent(user, entityId, this.cacheManager.getVehicleHealth(entityId), true);
        } else {
            // With the Entity Attach packet, the entity ID is set to -1 when the entity is detached;
            // Thus we need to retrieve the vehicle we stepped of by using a reverse lookup by passenger ID
            int reversedEntityId = this.cacheManager.getEntityIdByPassengerId(passengerId);
            this.cacheManager.updateVehiclePassenger(reversedEntityId, -1);

            if (user.getEntityId() == passengerId) {
                handlePassengerEvent(user, reversedEntityId, 0.5F, false);
            }
        }
    }

    private void handleEntityDestroy(WrapperPlayServerDestroyEntities packet, Object player) {
        for (int entityId : packet.getEntityIds()) {
            if (this.cacheManager.isLivingEntityCached(entityId)) {
                // Schedule this 2 ticks later because Bukkit updates intervals 1 tick later
                this.platform.getScheduler().rynAsyncTaskDelayed(task -> {
                    if (this.platform.isEntityRemoved(entityId, player)) {
                        this.cacheManager.removeLivingEntity(entityId);
                    }
                }, 100, TimeUnit.MILLISECONDS);
            }
        }
    }

    private LivingEntityData createLivingEntity(EntityType entityType) {
        LivingEntityData entityData;

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.WOLF)) {
            entityData = new WolfData();
        } else if (RidableEntities.RIDABLE_ENTITY_TYPES.contains(entityType)) {
            entityData = new RidableEntityData();
        } else {
            entityData = new LivingEntityData();
        }

        entityData.setEntityType(entityType);
        return entityData;
    }

    private void handlePassengerEvent(User user, int vehicleId, float healthValue, boolean entering) {
        List<EntityData> metadata = new ArrayList<>();

        if (!entering) {
            if (isBypassEnabled) {
                if (this.platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass")) return;
            }
        }

        metadata.add(new EntityData(MetadataIndex.HEALTH, EntityDataTypes.FLOAT, healthValue));
        user.sendPacketSilently(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
    }
}