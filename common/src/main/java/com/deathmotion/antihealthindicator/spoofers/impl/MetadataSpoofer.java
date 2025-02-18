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

        // Do not process if the packet refers to the user’s own entity or if the user has bypass permissions.
        if (entityId == player.user.getEntityId()) return;

        CachedEntity cachedEntity = entityCache.getCachedEntity(entityId).orElse(null);
        if (cachedEntity == null) return;

        EntityType entityType = cachedEntity.getEntityType();
        if (shouldIgnoreEntity(entityType, entityId, cachedEntity, settings)) return;

        packet.getEntityMetadata().forEach(entityData -> handleEntityMetadata(entityType, entityData, settings));
        event.markForReEncode(true);
    }

    private boolean shouldIgnoreEntity(EntityType entityType, int entityId, CachedEntity cachedEntity, Settings settings) {
        // Ignore entities with a boss bar (that shows the health already anyway)
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
        return entityType == EntityTypes.WOLF && settings.getEntityData().getWolves().isEnabled() && shouldIgnoreWolf(cachedEntity, settings);
    }

    private boolean shouldIgnoreWolf(CachedEntity cachedEntity, Settings settings) {
        WolfEntity wolfEntity = (WolfEntity) cachedEntity;
        boolean ignoreBasedOnSettings = !settings.getEntityData().getWolves().isTamed() && !settings.getEntityData().getWolves().isOwner();
        boolean isTamed = settings.getEntityData().getWolves().isTamed() && wolfEntity.isTamed();
        boolean isOwnedByUser = settings.getEntityData().getWolves().isOwner() && wolfEntity.isOwnerPresent() && wolfEntity.getOwnerUUID().equals(player.uuid);
        return ignoreBasedOnSettings || isTamed || isOwnedByUser;
    }

    /**
     * Modifies the metadata for the given entity based on its type and settings.
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
     * Applies default spoofing logic for common entity metadata.
     */
    private void applyDefaultSpoofing(EntityData entityData, Settings settings) {
        updateAirTicks(entityData, settings);
        if (entityData.getIndex() == player.metadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            float health = (Float) entityData.getValue();
            if (health > 0) {
                entityData.setValue(0.5f);
            }
        }
    }

    /**
     * Modifies the metadata for iron golems gradually.
     */
    private void spoofIronGolemMetadata(EntityData entityData, Settings settings) {
        updateAirTicks(entityData, settings);
        if (entityData.getIndex() == player.metadataIndex.HEALTH && settings.getEntityData().isHealth()) {
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
    private void spoofPlayerMetadata(EntityData entityData, Settings settings) {
        if (entityData.getIndex() == player.metadataIndex.ABSORPTION && settings.getEntityData().isAbsorption()) {
            setEntityDataValue(entityData, (byte) 0);
        }
        if (entityData.getIndex() == player.metadataIndex.XP && settings.getEntityData().isXp()) {
            setEntityDataValue(entityData, (byte) 0);
        }
    }

    /**
     * Updates the air ticks metadata if enabled.
     */
    private void updateAirTicks(EntityData entityData, Settings settings) {
        if (entityData.getIndex() == player.metadataIndex.AIR_TICKS && settings.getEntityData().isAirTicks()) {
            setEntityDataValue(entityData, (byte) 1);
        }
    }

    /**
     * Sets a new value for the entity data while preserving its original type.
     * <p>
     * This method ensures that the provided value is cast correctly to match
     * the original type of the metadata value.
     * Since entity metadata values
     * can be stored as different numeric types (e.g., Byte, Short, Integer),
     * this method uses the class of the current value to perform a safe cast.
     * <p>
     * This prevents potential `ClassCastException` issues when modifying metadata.
     *
     * @param entityData The entity metadata object to modify.
     * @param value      The new value to set, which will be cast to the original metadata type.
     */
    private void setEntityDataValue(EntityData entityData, byte value) {
        entityData.setValue(entityData.getValue().getClass().cast(value));
    }

}
