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

package com.deathmotion.antihealthindicator.packetlisteners.spoofers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.data.cache.CachedEntity;
import com.deathmotion.antihealthindicator.data.cache.WolfEntity;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;

/**
 * Listens for EntityMetadata events to modify the display of entity attributes.
 *
 * @param <P> The platform type.
 */
public class EntityMetadataListener<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final Settings settings;
    private final CacheManager<P> cacheManager;

    private final boolean healthTexturesSupported;

    /**
     * Constructs a new EntityMetadataListener with the specified {@link AHIPlatform}.
     *
     * @param platform The platform to use.
     */
    public EntityMetadataListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.settings = platform.getConfigManager().getSettings();
        this.cacheManager = platform.getCacheManager();

        this.healthTexturesSupported = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_15);

        platform.getLogManager().debug("Entity Metadata listener initialized.");
    }

    /**
     * Called when an {@link PacketSendEvent} is triggered to overwrite the {@link EntityData} for certain entities.
     *
     * @param event The event that has been triggered.
     */
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
        int entityId = packet.getEntityId();
        User user = event.getUser();

        if (entityId == user.getEntityId()) return;
        if (shouldBypass(user)) return;

        CachedEntity cachedEntity = cacheManager.getCachedEntity(user.getUUID(), entityId).orElse(null);
        if (cachedEntity == null) return;

        EntityType entityType = cachedEntity.getEntityType();
        if (shouldIgnoreEntity(entityType, user, entityId, cachedEntity)) return;

        packet.getEntityMetadata().forEach(entityData -> handleEntityMetadata(entityType, entityData));
        event.markForReEncode(true);
    }

    private boolean shouldBypass(User user) {
        return settings.isAllowBypass() && platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass");
    }

    private boolean shouldIgnoreEntity(EntityType entityType, User user, int entityId, CachedEntity cachedEntity) {
        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) return true;
        if (settings.getEntityData().isPlayersOnly() && entityType != EntityTypes.PLAYER) return true;
        if (!settings.getEntityData().isPlayersOnly() && settings.getEntityData().isIgnoreVehicles() && cacheManager.isUserPassenger(user.getUUID(), entityId, user.getEntityId()))
            return true;
        return entityType == EntityTypes.WOLF && settings.getEntityData().getWolves().isEnabled() && shouldIgnoreWolf(user, cachedEntity);
    }

    private boolean shouldIgnoreWolf(User user, CachedEntity cachedEntity) {
        WolfEntity wolfEntityData = (WolfEntity) cachedEntity;

        return (!settings.getEntityData().getWolves().isTamed() && !settings.getEntityData().getWolves().isOwner()) ||
                (settings.getEntityData().getWolves().isTamed() && wolfEntityData.isTamed()) ||
                (settings.getEntityData().getWolves().isOwner() && wolfEntityData.isOwnerPresent() && wolfEntityData.getOwnerUUID().equals(user.getUUID()));
    }

    private void handleEntityMetadata(EntityType entityType, EntityData entityData) {
        if (entityType == EntityTypes.IRON_GOLEM && settings.getEntityData().getIronGolems().isEnabled()) {
            if (!settings.getEntityData().getIronGolems().isGradual() || !healthTexturesSupported) {
                spoofEntityMetadata(entityData);
            } else {
                spoofIronGolemMetadata(entityData);
            }
        } else {
            spoofEntityMetadata(entityData);
            if (entityType == EntityTypes.PLAYER) {
                spoofPlayerMetadata(entityData);
            }
        }
    }

    private void spoofIronGolemMetadata(EntityData entityData) {
        if (entityData.getIndex() == MetadataIndex.AIR_TICKS && settings.getEntityData().isAirTicks()) {
            setDynamicValue(entityData, 1);
        }
        if (entityData.getIndex() == MetadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            float health = (float) entityData.getValue();
            entityData.setValue(health > 74 ? 100f : health > 49 ? 74f : health > 24 ? 49f : 24f);
        }
    }

    private void spoofEntityMetadata(EntityData entityData) {
        if (entityData.getIndex() == MetadataIndex.AIR_TICKS && settings.getEntityData().isAirTicks()) {
            setDynamicValue(entityData, 1);
        }
        if (entityData.getIndex() == MetadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            if (((Float) entityData.getValue()) > 0) {
                entityData.setValue(0.5f);
            }
        }
    }

    private void spoofPlayerMetadata(EntityData entityData) {
        if (entityData.getIndex() == MetadataIndex.ABSORPTION && settings.getEntityData().isAbsorption()) {
            setDynamicValue(entityData, 0);
        }
        if (entityData.getIndex() == MetadataIndex.XP && settings.getEntityData().isXp()) {
            setDynamicValue(entityData, 0);
        }
    }

    /**
     * This method is designed to handle and set dynamic values for different types of data objects.
     * This is necessary because the value of the data object is stored as an Object, and not as a primitive type.
     * Besides, the value of the data object is not always an integer,
     * but can be a float, double, long, short or byte, etc.
     * <p>
     * The reason why I am not simply using a byte since my value will never be bigger than zero or one is because
     * legacy versions for some retarded reason don't support upcasting of bytes to integers.
     *
     * @param obj        The EntityData containing the value to be set.
     * @param spoofValue The value to be set in the EntityData object.
     */
    private void setDynamicValue(EntityData obj, int spoofValue) {
        Object value = obj.getValue();

        if (value instanceof Integer) {
            obj.setValue(spoofValue);
        } else if (value instanceof Short) {
            obj.setValue((short) spoofValue);
        } else if (value instanceof Byte) {
            obj.setValue((byte) spoofValue);
        } else if (value instanceof Long) {
            obj.setValue((long) spoofValue);
        } else if (value instanceof Float) {
            obj.setValue((float) spoofValue);
        } else if (value instanceof Double) {
            obj.setValue((double) spoofValue);
        }
    }
}
