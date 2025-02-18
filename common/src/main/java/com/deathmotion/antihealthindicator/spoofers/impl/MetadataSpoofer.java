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

package com.deathmotion.antihealthindicator.spoofers.impl;

import com.deathmotion.antihealthindicator.cache.EntityCache;
import com.deathmotion.antihealthindicator.cache.entities.CachedEntity;
import com.deathmotion.antihealthindicator.cache.entities.WolfEntity;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.deathmotion.antihealthindicator.spoofers.type.PacketSpoofer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;

public class MetadataSpoofer extends Spoofer implements PacketSpoofer {

    // Constants for Iron Golem health thresholds
    private static final float IRON_GOLEM_HEALTH_MAX = 100f;
    private static final float IRON_GOLEM_THRESHOLD_1 = 74f;
    private static final float IRON_GOLEM_THRESHOLD_2 = 49f;
    private static final float IRON_GOLEM_THRESHOLD_3 = 24f;

    private final EntityCache entityCache;
    private final boolean healthTexturesSupported;

    public MetadataSpoofer(AHIPlayer player) {
        super(player);
        this.entityCache = player.entityCache;
        this.healthTexturesSupported = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_15);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) return;

        Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled()) return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
        int entityId = packet.getEntityId();

        // Skip processing if the packet refers to the user's own entity.
        if (entityId == player.user.getEntityId()) return;

        CachedEntity cachedEntity = entityCache.getCachedEntity(entityId).orElse(null);
        if (cachedEntity == null) return;

        EntityType entityType = cachedEntity.getEntityType();
        if (shouldIgnoreEntity(entityType, entityId, cachedEntity, settings)) return;

        // Process each metadata entry for spoofing.
        packet.getEntityMetadata().forEach(entityData -> handleEntityMetadata(entityType, entityData, settings));

        event.markForReEncode(true);
    }

    private boolean shouldIgnoreEntity(EntityType entityType, int entityId, CachedEntity cachedEntity, Settings settings) {
        // Ignore entities with built-in health displays (e.g., bosses).
        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) {
            return true;
        }

        // If only players should be processed, skip non-player entities.
        if (settings.getEntityData().isPlayersOnly() && entityType != EntityTypes.PLAYER) {
            return true;
        }

        // Optionally ignore vehicles.
        if (!settings.getEntityData().isPlayersOnly() && settings.getEntityData().isIgnoreVehicles() && entityCache.isUserPassenger(entityId)) {
            return true;
        }

        // Special handling for wolves.
        if (entityType == EntityTypes.WOLF && settings.getEntityData().getWolves().isEnabled()) {
            return shouldIgnoreWolf(cachedEntity, settings);
        }

        return false;
    }

    /**
     * Determines whether a wolf entity should be ignored based on its tamed/owner state and the settings.
     */
    private boolean shouldIgnoreWolf(CachedEntity cachedEntity, Settings settings) {
        WolfEntity wolfEntity = (WolfEntity) cachedEntity;
        Settings.EntityData.Wolves wolfSettings = settings.getEntityData().getWolves();

        // If neither tamed nor owner conditions are enabled, ignore the wolf.
        if (!wolfSettings.isTamed() && !wolfSettings.isOwner()) {
            return true;
        }
        // Ignore if the wolf is tamed and tamed wolves should be ignored.
        if (wolfSettings.isTamed() && wolfEntity.isTamed()) {
            return true;
        }

        // Ignore if the user owns the wolf and owner wolves should be ignored.
        return wolfSettings.isOwner() && wolfEntity.isOwnerPresent() && wolfEntity.getOwnerUUID().equals(player.uuid);
    }

    /**
     * Modifies the metadata for the given entity based on its type and the configured settings.
     */
    private void handleEntityMetadata(EntityType entityType, EntityData entityData, Settings settings) {
        if (entityType == EntityTypes.IRON_GOLEM && settings.getEntityData().getIronGolems().isEnabled()) {
            if (!settings.getEntityData().getIronGolems().isGradual() || !healthTexturesSupported) {
                applyDefaultSpoofing(entityData, settings);
            } else {
                spoofIronGolemMetadata(entityData, settings);
            }
        } else {
            applyDefaultSpoofing(entityData, settings);
            if (entityType == EntityTypes.PLAYER) {
                spoofPlayerMetadata(entityData, settings);
            }
        }
    }

    /**
     * Applies default spoofing logic to common metadata.
     */
    private void applyDefaultSpoofing(EntityData entityData, Settings settings) {
        updateAirTicks(entityData, settings);
        if (entityData.getIndex() == player.metadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            float health = (Float) entityData.getValue();
            if (health > 0) {
                // Spoof health value to a fixed, low value.
                entityData.setValue(0.5f);
            }
        }
    }

    /**
     * Applies gradual spoofing for iron golem health based on thresholds.
     */
    private void spoofIronGolemMetadata(EntityData entityData, Settings settings) {
        updateAirTicks(entityData, settings);
        if (entityData.getIndex() == player.metadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            float health = (Float) entityData.getValue();
            if (health > IRON_GOLEM_THRESHOLD_1) {
                entityData.setValue(IRON_GOLEM_HEALTH_MAX);
            } else if (health > IRON_GOLEM_THRESHOLD_2) {
                entityData.setValue(IRON_GOLEM_THRESHOLD_1);
            } else if (health > IRON_GOLEM_THRESHOLD_3) {
                entityData.setValue(IRON_GOLEM_THRESHOLD_2);
            } else {
                entityData.setValue(IRON_GOLEM_THRESHOLD_3);
            }
        }
    }

    /**
     * Spoofs player-specific metadata such as absorption and experience.
     */
    private void spoofPlayerMetadata(EntityData entityData, Settings settings) {
        if (entityData.getIndex() == player.metadataIndex.ABSORPTION && settings.getEntityData().isAbsorption()) {
            setDynamicValue(entityData, 0);
        }
        if (entityData.getIndex() == player.metadataIndex.XP && settings.getEntityData().isXp()) {
            setDynamicValue(entityData, 0);
        }
    }

    /**
     * Updates the air ticks metadata if enabled in the settings.
     */
    private void updateAirTicks(EntityData entityData, Settings settings) {
        if (entityData.getIndex() == player.metadataIndex.AIR_TICKS && settings.getEntityData().isAirTicks()) {
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
