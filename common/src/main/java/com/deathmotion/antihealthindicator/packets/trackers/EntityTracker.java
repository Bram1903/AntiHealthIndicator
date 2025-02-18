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

package com.deathmotion.antihealthindicator.packets.trackers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.RidableEntities;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.data.cache.CachedEntity;
import com.deathmotion.antihealthindicator.data.cache.RidableEntity;
import com.deathmotion.antihealthindicator.data.cache.WolfEntity;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.*;

/**
 * Listens for EntityState events and manages the caching of various entity state details.
 */
public class EntityTracker {
    private final AHIPlayer player;
    private final CacheManager cacheManager;
    private final ConfigManager<?> configManager;


    public EntityTracker(AHIPlayer player, CacheManager cacheManager) {
        this.player = player;
        this.cacheManager = cacheManager;
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
            handleEntityMetadata(new WrapperPlayServerEntityMetadata(event), event.getUser(), settings);
        } else if (PacketType.Play.Server.DESTROY_ENTITIES == type) {
            handleDestroyEntities(new WrapperPlayServerDestroyEntities(event));
        } else if (PacketType.Play.Server.RESPAWN == type) {
            handleRespawn();
        } else if (PacketType.Play.Server.JOIN_GAME == type) {
            handleJoinGame();
        } else if (PacketType.Play.Server.CONFIGURATION_START == type) {
            handleConfigurationStart();
        }
    }

    private void handleSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet, Settings settings) {
        EntityType entityType = packet.getEntityType();

        if (settings.getEntityData().isPlayersOnly()) {
            if (!EntityTypes.isTypeInstanceOf(entityType, EntityTypes.PLAYER)) return;
        }

        int entityId = packet.getEntityId();

        CachedEntity entityData = createLivingEntity(entityType);
        cacheManager.addLivingEntity(entityId, entityData);
    }

    private void handleSpawnEntity(WrapperPlayServerSpawnEntity packet, Settings settings) {
        EntityType entityType = packet.getEntityType();

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.LIVINGENTITY)) {
            if (settings.getEntityData().isPlayersOnly()) {
                if (!EntityTypes.isTypeInstanceOf(entityType, EntityTypes.PLAYER)) return;
            }

            int entityId = packet.getEntityId();

            CachedEntity entityData = createLivingEntity(entityType);
            cacheManager.addLivingEntity(entityId, entityData);
        }
    }

    private void handleSpawnPlayer(WrapperPlayServerSpawnPlayer packet) {
        CachedEntity livingEntityData = new CachedEntity();
        livingEntityData.setEntityType(EntityTypes.PLAYER);

        cacheManager.addLivingEntity(packet.getEntityId(), livingEntityData);
    }

    private void handleEntityMetadata(WrapperPlayServerEntityMetadata packet, User user, Settings settings) {
        if (settings.getEntityData().isPlayersOnly()) return;

        int entityId = packet.getEntityId();

        CachedEntity entityData = cacheManager.getCachedEntity(entityId).orElse(null);
        if (entityData == null) return;

        packet.getEntityMetadata().forEach(metaData -> entityData.processMetaData(metaData, player));
    }

    private void handleDestroyEntities(WrapperPlayServerDestroyEntities packet) {
        for (int entityId : packet.getEntityIds()) {
            cacheManager.removeEntity(entityId);
        }
    }

    private void handleRespawn() {
        cacheManager.resetUserCache();
    }

    private void handleJoinGame() {
        cacheManager.resetUserCache();
    }

    private void handleConfigurationStart() {
        cacheManager.resetUserCache();
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