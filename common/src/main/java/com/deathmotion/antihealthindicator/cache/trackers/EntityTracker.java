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
import com.deathmotion.antihealthindicator.cache.entities.RidableEntity;
import com.deathmotion.antihealthindicator.cache.entities.WolfEntity;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.RidableEntities;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
import com.github.retrooper.packetevents.wrapper.play.server.*;

/**
 * Listens for EntityState events and manages the caching of various entity state details.
 */
public class EntityTracker {
    private final AHIPlayer player;
    private final EntityCache entityCache;
    private final ConfigManager<?> configManager;

    private DimensionType currentDimension;

    public EntityTracker(AHIPlayer player, EntityCache entityCache) {
        this.player = player;
        this.entityCache = entityCache;
        this.configManager = AHIPlatform.getInstance().getConfigManager();
    }

    public void onPacketSend(PacketSendEvent event) {
        final Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled()) return;

        final PacketTypeCommon type = event.getPacketType();

        if (PacketType.Play.Server.SPAWN_LIVING_ENTITY == type) {
            handleSpawnLivingEntity(new WrapperPlayServerSpawnLivingEntity(event), settings);
        } else if (PacketType.Play.Server.SPAWN_ENTITY == type) {
            handleSpawnEntity(new WrapperPlayServerSpawnEntity(event), settings);
        } else if (PacketType.Play.Server.SPAWN_PLAYER == type) {
            handleSpawnPlayer(new WrapperPlayServerSpawnPlayer(event));
        } else if (PacketType.Play.Server.ENTITY_METADATA == type) {
            handleEntityMetadata(new WrapperPlayServerEntityMetadata(event), settings);
        } else if (PacketType.Play.Server.DESTROY_ENTITIES == type) {
            handleDestroyEntities(new WrapperPlayServerDestroyEntities(event));
        } else if (PacketType.Play.Server.RESPAWN == type) {
            handleRespawn(new WrapperPlayServerRespawn(event));
        } else if (PacketType.Play.Server.JOIN_GAME == type) {
            handleJoinGame(new WrapperPlayServerJoinGame(event));
        } else if (PacketType.Play.Server.CONFIGURATION_START == type) {
            clearCache();
        }
    }

    private void handleSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet, Settings settings) {
        EntityType entityType = packet.getEntityType();
        if (settings.getEntityData().isPlayersOnly() && !EntityTypes.isTypeInstanceOf(entityType, EntityTypes.PLAYER))
            return;
        spawnEntity(packet.getEntityId(), entityType);
    }

    private void handleSpawnEntity(WrapperPlayServerSpawnEntity packet, Settings settings) {
        EntityType entityType = packet.getEntityType();
        if (!EntityTypes.isTypeInstanceOf(entityType, EntityTypes.LIVINGENTITY)) return;
        if (settings.getEntityData().isPlayersOnly() && !EntityTypes.isTypeInstanceOf(entityType, EntityTypes.PLAYER))
            return;

        spawnEntity(packet.getEntityId(), entityType);
    }

    private void handleSpawnPlayer(WrapperPlayServerSpawnPlayer packet) {
        spawnEntity(packet.getEntityId(), EntityTypes.PLAYER);
    }

    private void spawnEntity(int entityId, EntityType entityType) {
        CachedEntity entityData = createLivingEntity(entityType);
        entityCache.addLivingEntity(entityId, entityData);
    }

    private void handleEntityMetadata(WrapperPlayServerEntityMetadata packet, Settings settings) {
        if (settings.getEntityData().isPlayersOnly()) return;

        int entityId = packet.getEntityId();
        CachedEntity entityData = entityCache.getEntityRaw(entityId);
        if (entityData == null) return;

        packet.getEntityMetadata().forEach(metaData -> entityData.processMetaData(metaData, player));
    }

    private void handleDestroyEntities(WrapperPlayServerDestroyEntities packet) {
        for (int entityId : packet.getEntityIds()) {
            entityCache.removeEntity(entityId);
        }
    }

    private void handleRespawn(WrapperPlayServerRespawn packet) {
        DimensionType dimension = packet.getDimensionType();

        if (!dimension.equals(currentDimension)) {
            clearCache();
        }

        currentDimension = dimension;
    }

    private void handleJoinGame(WrapperPlayServerJoinGame packet) {
        currentDimension = packet.getDimensionType();
        clearCache();
    }

    private void clearCache() {
        entityCache.resetUserCache();
    }

    private CachedEntity createLivingEntity(EntityType entityType) {
        CachedEntity entityData;
        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.WOLF)) {
            entityData = new WolfEntity();
        } else if (RidableEntities.isRideable(entityType)) {
            entityData = new RidableEntity();
        } else {
            entityData = new CachedEntity();
        }
        entityData.setEntityType(entityType);
        return entityData;
    }
}