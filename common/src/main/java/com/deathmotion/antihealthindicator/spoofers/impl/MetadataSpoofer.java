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
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;

public class MetadataSpoofer extends Spoofer implements PacketSpoofer {

    private static final float IRON_GOLEM_HEALTH_MAX = 100f;
    private static final float IRON_GOLEM_THRESHOLD_1 = 74f;
    private static final float IRON_GOLEM_THRESHOLD_2 = 49f;
    private static final float IRON_GOLEM_THRESHOLD_3 = 24f;

    private final EntityCache entityCache;
    private final boolean healthTexturesSupported;

    public MetadataSpoofer(AHIPlayer player) {
        super(player);
        this.entityCache = player.entityCache;
        this.healthTexturesSupported = player.user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15);
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

        packet.getEntityMetadata().forEach(entityData -> handleEntityMetadata(entityType, entityData, settings));
        event.markForReEncode(true);
    }

    private boolean shouldIgnoreEntity(EntityType entityType, int entityId, CachedEntity cachedEntity, Settings settings) {
        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) return true;

        if (settings.getEntityData().isPlayersOnly() && entityType != EntityTypes.PLAYER) return true;

        if (!settings.getEntityData().isPlayersOnly()
                && settings.getEntityData().isIgnoreVehicles()
                && entityCache.isUserPassenger(entityId)) {
            return true;
        }

        if (entityType == EntityTypes.WOLF && settings.getEntityData().getWolves().isEnabled()) {
            return shouldIgnoreWolf(cachedEntity, settings);
        }

        return false;
    }

    private boolean shouldIgnoreWolf(CachedEntity cachedEntity, Settings settings) {
        WolfEntity wolfEntity = (WolfEntity) cachedEntity;
        Settings.EntityData.Wolves wolfSettings = settings.getEntityData().getWolves();

        if (!wolfSettings.isTamed() && !wolfSettings.isOwner()) return true;
        if (wolfSettings.isTamed() && wolfEntity.isTamed()) return true;
        return wolfSettings.isOwner()
                && wolfEntity.isOwnerPresent()
                && wolfEntity.getOwnerUUID().equals(player.uuid);
    }

    private void handleEntityMetadata(EntityType entityType, EntityData<?> entityData, Settings settings) {
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

    @SuppressWarnings("unchecked")
    private void applyDefaultSpoofing(EntityData<?> entityData, Settings settings) {
        updateAirTicks(entityData, settings);
        if (entityData.getIndex() == player.metadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            Object value = entityData.getValue();
            if (value instanceof Float && (Float) value > 0f) {
                ((EntityData<Float>) entityData).setValue(0.5f);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void spoofIronGolemMetadata(EntityData<?> entityData, Settings settings) {
        updateAirTicks(entityData, settings);
        if (entityData.getIndex() == player.metadataIndex.HEALTH && settings.getEntityData().isHealth()) {
            Object value = entityData.getValue();
            if (value instanceof Float) {
                float health = (Float) value;
                if (health > IRON_GOLEM_THRESHOLD_1) {
                    ((EntityData<Float>) entityData).setValue(IRON_GOLEM_HEALTH_MAX);
                } else if (health > IRON_GOLEM_THRESHOLD_2) {
                    ((EntityData<Float>) entityData).setValue(IRON_GOLEM_THRESHOLD_1);
                } else if (health > IRON_GOLEM_THRESHOLD_3) {
                    ((EntityData<Float>) entityData).setValue(IRON_GOLEM_THRESHOLD_2);
                } else {
                    ((EntityData<Float>) entityData).setValue(IRON_GOLEM_THRESHOLD_3);
                }
            }
        }
    }


    private void spoofPlayerMetadata(EntityData<?> entityData, Settings settings) {
        if (entityData.getIndex() == player.metadataIndex.ABSORPTION && settings.getEntityData().isAbsorption()) {
            setDynamicValue(entityData, 0);
        }
        if (entityData.getIndex() == player.metadataIndex.XP && settings.getEntityData().isXp()) {
            setDynamicValue(entityData, 0);
        }
    }

    private void updateAirTicks(EntityData<?> entityData, Settings settings) {
        if (entityData.getIndex() == player.metadataIndex.AIR_TICKS && settings.getEntityData().isAirTicks()) {
            setDynamicValue(entityData, 1);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void setDynamicValue(EntityData<?> entityData, int spoofValue) {
        Object value = entityData.getValue();

        if (value instanceof Integer) {
            ((EntityData<Integer>) entityData).setValue(spoofValue);
        } else if (value instanceof Short) {
            ((EntityData<Short>) entityData).setValue((short) spoofValue);
        } else if (value instanceof Byte) {
            ((EntityData<Byte>) entityData).setValue((byte) spoofValue);
        } else if (value instanceof Long) {
            ((EntityData<Long>) entityData).setValue((long) spoofValue);
        } else if (value instanceof Float) {
            ((EntityData<Float>) entityData).setValue((float) spoofValue);
        } else if (value instanceof Double) {
            ((EntityData<Double>) entityData).setValue((double) spoofValue);
        }
    }
}
