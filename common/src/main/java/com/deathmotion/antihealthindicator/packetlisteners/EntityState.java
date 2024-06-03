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
import com.deathmotion.antihealthindicator.data.cache.CachedEntity;
import com.deathmotion.antihealthindicator.data.cache.RidableEntity;
import com.deathmotion.antihealthindicator.data.cache.WolfEntity;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.LogManager;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.*;

/**
 * Listens for EntityState events and manages the caching of various entity state details.
 *
 * @param <P> The platform type.
 */
public class EntityState<P> implements PacketListener {
    private final CacheManager<P> cacheManager;
    private final LogManager<P> logManager;

    private final boolean playersOnly;

    /**
     * Constructs a new EntityState with the specified {@link AHIPlatform}.
     *
     * @param platform The platform to use.
     */
    public EntityState(AHIPlatform<P> platform) {
        this.cacheManager = platform.getCacheManager();
        this.logManager = platform.getLogManager();

        this.playersOnly = platform.getConfigurationOption(ConfigOption.PLAYER_ONLY);

        platform.getLogManager().debug("Entity State listener has been set up.");
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

        if (PacketType.Play.Server.SPAWN_PLAYER == type) {
            handleSpawnPlayer(new WrapperPlayServerSpawnPlayer(event), event.getUser());
        } else if (PacketType.Play.Server.DESTROY_ENTITIES == type) {
            handleDestroyEntities(new WrapperPlayServerDestroyEntities(event), event.getUser());
        } else if (PacketType.Play.Server.RESPAWN == type) {
            handleRespawn(event.getUser());
        } else if (PacketType.Play.Server.JOIN_GAME == type) {
            handleJoinGame(event.getUser());
        } else if (PacketType.Play.Server.CONFIGURATION_START == type) {
            handleConfigurationStart(event.getUser());
        }

        if (!playersOnly) {
            if (PacketType.Play.Server.SPAWN_LIVING_ENTITY == type) {
                handleSpawnLivingEntity(new WrapperPlayServerSpawnLivingEntity(event), event.getUser());
            } else if (PacketType.Play.Server.SPAWN_ENTITY == type) {
                handleSpawnEntity(new WrapperPlayServerSpawnEntity(event), event.getUser());
            } else if (PacketType.Play.Server.ENTITY_METADATA == type) {
                handleEntityMetadata(new WrapperPlayServerEntityMetadata(event), event.getUser());
            }
        }
    }

    private void handleSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet, User user) {
        int entityId = packet.getEntityId();
        EntityType entityType = packet.getEntityType();

        CachedEntity entityData = createLivingEntity(entityType);
        cacheManager.addLivingEntity(user, entityId, entityData);
    }

    private void handleSpawnEntity(WrapperPlayServerSpawnEntity packet, User user) {
        EntityType entityType = packet.getEntityType();

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.LIVINGENTITY)) {
            int entityId = packet.getEntityId();

            CachedEntity entityData = createLivingEntity(entityType);
            cacheManager.addLivingEntity(user, entityId, entityData);
        }
    }

    private void handleSpawnPlayer(WrapperPlayServerSpawnPlayer packet, User user) {
        CachedEntity livingEntityData = new CachedEntity();
        livingEntityData.setEntityType(EntityTypes.PLAYER);

        cacheManager.addLivingEntity(user, packet.getEntityId(), livingEntityData);
    }

    private void handleEntityMetadata(WrapperPlayServerEntityMetadata packet, User user) {
        int entityId = packet.getEntityId();

        CachedEntity entityData = cacheManager.getCachedEntity(user, entityId).orElse(null);
        if (entityData == null) return;

        packet.getEntityMetadata().forEach(metaData -> entityData.processMetaData(metaData, user));
    }

    private void handleDestroyEntities(WrapperPlayServerDestroyEntities packet, User user) {
        for (int entityId : packet.getEntityIds()) {
            cacheManager.removeEntity(user, entityId);
        }
    }

    private void handleRespawn(User user) {
        cacheManager.resetUserCache(user);
    }

    private void handleJoinGame(User user) {
        cacheManager.resetUserCache(user);
    }

    private void handleConfigurationStart(User user) {
        cacheManager.resetUserCache(user);
    }

    private CachedEntity createLivingEntity(EntityType entityType) {
        CachedEntity entityData;

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.WOLF)) {
            entityData = new WolfEntity();
        } else if (RidableEntities.RIDABLE_ENTITY_TYPES.contains(entityType)) {
            entityData = new RidableEntity();
        } else {
            entityData = new CachedEntity();
        }

        entityData.setEntityType(entityType);
        return entityData;
    }
}