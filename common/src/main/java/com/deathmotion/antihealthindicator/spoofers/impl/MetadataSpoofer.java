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
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.deathmotion.antihealthindicator.models.Settings;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.jetbrains.annotations.NotNull;

public final class MetadataSpoofer extends Spoofer {

    private final EntityCache entityCache;
    private final boolean healthTexturesSupported;

    public MetadataSpoofer(AHIPlayer player) {
        super(player);
        this.entityCache = player.entityCache;
        this.healthTexturesSupported = player.user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15);
    }

    @SuppressWarnings("unchecked")
    private static <T> void setValue(EntityData<?> data, T spoofValue) {
        ((EntityData<@NotNull T>) data).setValue(spoofValue);
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

        CachedEntity cachedEntity = entityCache.getEntity(entityId);
        if (cachedEntity == null) return;

        if (shouldIgnoreEntity(entityId, cachedEntity, settings)) return;

        packet.getEntityMetadata().forEach(entityData -> handleEntityMetadata(cachedEntity, entityData, settings));
        event.markForReEncode(true);
    }

    private boolean shouldIgnoreEntity(int entityId, CachedEntity cachedEntity, Settings settings) {
        final EntityType entityType = cachedEntity.getEntityType();

        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) {
            return true;
        }

        if (settings.getEntityData().isPlayersOnly() && entityType != EntityTypes.PLAYER) {
            return true;
        }

        if (!settings.getEntityData().isPlayersOnly() && settings.getEntityData().isIgnoreVehicles() && entityCache.getCurrentVehicleId().map(currentVehicleId -> currentVehicleId == entityId).orElse(false)) {
            return true;
        }

        if (entityType == EntityTypes.WOLF) {
            WolfEntity wolfEntity = (WolfEntity) cachedEntity;
            return (wolfEntity.shouldIgnoreWolf(player.uuid, settings));
        }

        return false;
    }

    private void handleEntityMetadata(CachedEntity cachedEntity, EntityData<?> entityData, Settings settings) {
        final EntityType entityType = cachedEntity.getEntityType();
        updateAirTicks(entityData, settings);

        if (entityType == EntityTypes.IRON_GOLEM && settings.getEntityData().getIronGolems().isEnabled()) {
            if (!settings.getEntityData().getIronGolems().isGradual() || !healthTexturesSupported) {
                applyDefaultSpoofing(entityData, settings);
            } else {
                spoofIronGolemMetadata(cachedEntity, entityData, settings);
            }
        } else {
            applyDefaultSpoofing(entityData, settings);
            if (entityType == EntityTypes.PLAYER) {
                spoofPlayerMetadata(entityData, settings);
            }
        }
    }

    private void applyDefaultSpoofing(EntityData<?> entityData, Settings settings) {
        if (entityData.getIndex() != player.metadataIndex.HEALTH) return;
        if (!settings.getEntityData().isHealth()) return;

        Object value = entityData.getValue();
        if (value instanceof Float && (Float) value > 0f) {
            setValue(entityData, 0.5f);
        }
    }

    private void spoofIronGolemMetadata(CachedEntity cachedEntity, EntityData<?> entityData, Settings settings) {
        updateAirTicks(entityData, settings);

        if (entityData.getIndex() != player.metadataIndex.HEALTH || !settings.getEntityData().isHealth()) return;

        final Object value = entityData.getValue();
        if (!(value instanceof Float)) return;

        final float health = (Float) value;
        if (health <= 0f) return;

        final float maxHealth = cachedEntity.getMaxHealth();
        if (maxHealth <= 0f) return;

        final float ratio = health / maxHealth;
        final float spoofed;

        if (ratio >= 0.75f) {
            spoofed = maxHealth;          // No cracks
        } else if (ratio >= 0.50f) {
            spoofed = maxHealth * 0.74f;  // Minor cracks
        } else if (ratio >= 0.25f) {
            spoofed = maxHealth * 0.49f;  // Medium cracks
        } else {
            spoofed = maxHealth * 0.24f;  // Heavy cracks
        }

        setValue(entityData, spoofed);
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

    private void setDynamicValue(EntityData<?> entityData, int spoofValue) {
        Object value = entityData.getValue();

        if (value instanceof Integer) {
            setValue(entityData, spoofValue);
        } else if (value instanceof Short) {
            setValue(entityData, (short) spoofValue);
        } else if (value instanceof Byte) {
            setValue(entityData, (byte) spoofValue);
        } else if (value instanceof Long) {
            setValue(entityData, (long) spoofValue);
        } else if (value instanceof Float) {
            setValue(entityData, (float) spoofValue);
        } else if (value instanceof Double) {
            setValue(entityData, (double) spoofValue);
        }
    }
}
