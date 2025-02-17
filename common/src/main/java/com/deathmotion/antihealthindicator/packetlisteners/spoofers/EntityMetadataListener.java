/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2025 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
import com.deathmotion.antihealthindicator.managers.ConfigManager;
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
 * Listens for EntityMetadata packets and modifies certain entity attributes
 * based on configuration settings.
 *
 * @param <P> The platform type.
 */
public class EntityMetadataListener<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final ConfigManager<P> configManager;
    private final CacheManager<P> cacheManager;
    private final boolean healthTexturesSupported;

    public EntityMetadataListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.configManager = platform.getConfigManager();
        this.cacheManager = platform.getCacheManager();

        // Health textures are supported for server versions 1.15 and above.
        this.healthTexturesSupported = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_15);

        platform.getLogManager().debug("Entity Metadata listener initialized.");
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) return;

        Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled()) return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
        int entityId = packet.getEntityId();
        User user = event.getUser();

        // Do not process if the packet refers to the user’s own entity or if the user has bypass permissions.
        if (entityId == user.getEntityId() || shouldBypass(user, settings)) return;

        CachedEntity cachedEntity = cacheManager.getCachedEntity(user.getUUID(), entityId).orElse(null);
        if (cachedEntity == null) return;

        EntityType entityType = cachedEntity.getEntityType();
        if (shouldIgnoreEntity(entityType, user, entityId, cachedEntity, settings)) return;

        MetadataIndex metadataIndex = new MetadataIndex(user.getClientVersion());
        packet.getEntityMetadata().forEach(entityData -> handleEntityMetadata(entityType, entityData, metadataIndex, settings));
        event.markForReEncode(true);
    }

    private boolean shouldBypass(User user, Settings settings) {
        return settings.isAllowBypass() && platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass");
    }

    private boolean shouldIgnoreEntity(EntityType entityType, User user, int entityId, CachedEntity cachedEntity, Settings settings) {
        // Ignore entities with a boss bar (that shows the health already anyway)
        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) {
            return true;
        }

        // If only players should be processed, skip non-player entities.
        if (settings.getEntityData().isPlayersOnly() && entityType != EntityTypes.PLAYER) {
            return true;
        }

        // Optionally ignore vehicles.
        if (!settings.getEntityData().isPlayersOnly() && settings.getEntityData().isIgnoreVehicles() && cacheManager.isUserPassenger(user.getUUID(), entityId, user.getEntityId())) {
            return true;
        }

        // Special handling for wolves.
        return entityType == EntityTypes.WOLF && settings.getEntityData().getWolves().isEnabled() && shouldIgnoreWolf(user, cachedEntity, settings);
    }

    private boolean shouldIgnoreWolf(User user, CachedEntity cachedEntity, Settings settings) {
        WolfEntity wolfEntity = (WolfEntity) cachedEntity;
        boolean ignoreBasedOnSettings = !settings.getEntityData().getWolves().isTamed() && !settings.getEntityData().getWolves().isOwner();
        boolean isTamed = settings.getEntityData().getWolves().isTamed() && wolfEntity.isTamed();
        boolean isOwnedByUser = settings.getEntityData().getWolves().isOwner() && wolfEntity.isOwnerPresent() && wolfEntity.getOwnerUUID().equals(user.getUUID());
        return ignoreBasedOnSettings || isTamed || isOwnedByUser;
    }

    /**
     * Modifies the metadata for the given entity based on its type and settings.
     */
    private void handleEntityMetadata(EntityType entityType, EntityData entityData, MetadataIndex metadataIndex, Settings settings) {
        if (entityType == EntityTypes.IRON_GOLEM && settings.getEntityData().getIronGolems().isEnabled()) {
            if (!settings.getEntityData().getIronGolems().isGradual() || !healthTexturesSupported) {
                applyDefaultSpoofing(entityData, metadataIndex, settings);
            } else {
                spoofIronGolemMetadata(entityData, metadataIndex, settings);
            }
        } else {
            applyDefaultSpoofing(entityData, metadataIndex, settings);
            if (entityType == EntityTypes.PLAYER) {
                spoofPlayerMetadata(entityData, metadataIndex, settings);
            }
        }
    }

    /**
     * Applies default spoofing logic for common entity metadata.
     */
    private void applyDefaultSpoofing(EntityData entityData, MetadataIndex metadataIndex, Settings settings) {
        updateAirTicks(entityData, metadataIndex, settings);
        if (entityData.getIndex() == metadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            float health = (Float) entityData.getValue();
            if (health > 0) {
                entityData.setValue(0.5f);
            }
        }
    }

    /**
     * Modifies the metadata for iron golems gradually.
     */
    private void spoofIronGolemMetadata(EntityData entityData, MetadataIndex metadataIndex, Settings settings) {
        updateAirTicks(entityData, metadataIndex, settings);
        if (entityData.getIndex() == metadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            float health = (Float) entityData.getValue();
            if (health > 74f) {
                entityData.setValue(100f);
            } else if (health > 49f) {
                entityData.setValue(74f);
            } else if (health > 24f) {
                entityData.setValue(49f);
            } else {
                entityData.setValue(24f);
            }
        }
    }

    /**
     * Modifies the metadata for player entities.
     */
    private void spoofPlayerMetadata(EntityData entityData, MetadataIndex metadataIndex, Settings settings) {
        if (entityData.getIndex() == metadataIndex.ABSORPTION && settings.getEntityData().isAbsorption()) {
            setDynamicValue(entityData, 0);
        }
        if (entityData.getIndex() == metadataIndex.XP && settings.getEntityData().isXp()) {
            setDynamicValue(entityData, 0);
        }
    }

    /**
     * Updates the air ticks metadata if enabled.
     */
    private void updateAirTicks(EntityData entityData, MetadataIndex metadataIndex, Settings settings) {
        if (entityData.getIndex() == metadataIndex.AIR_TICKS && settings.getEntityData().isAirTicks()) {
            setDynamicValue(entityData, 1);
        }
    }

    /**
     * Sets a new value for the entity data while preserving its original numeric type.
     * <p>
     * This method is necessary because the metadata value is stored as an {@code Object}
     * and can be of different numeric types (e.g., Integer, Short, Byte, Long, Float, or Double).
     *
     * @param entityData The metadata object to modify.
     * @param spoofValue The new value to set.
     */
    private void setDynamicValue(EntityData entityData, int spoofValue) {
        Object value = entityData.getValue();

        if (value instanceof Integer) {
            entityData.setValue(spoofValue);
        } else if (value instanceof Short) {
            entityData.setValue((short) spoofValue);
        } else if (value instanceof Byte) {
            entityData.setValue((byte) spoofValue);
        } else if (value instanceof Long) {
            entityData.setValue((long) spoofValue);
        } else if (value instanceof Float) {
            entityData.setValue((float) spoofValue);
        } else if (value instanceof Double) {
            entityData.setValue((double) spoofValue);
        }
    }
}