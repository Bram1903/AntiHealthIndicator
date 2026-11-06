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
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.deathmotion.antihealthindicator.models.Settings;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.deathmotion.antihealthindicator.util.HealthUtil;
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
    private static <T> boolean setValue(EntityData<?> data, T spoofValue) {
        boolean changed = !spoofValue.equals(data.getValue());
        ((EntityData<@NotNull T>) data).setValue(spoofValue);
        return changed;
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

        boolean changed = false;
        for (EntityData<?> entityData : packet.getEntityMetadata()) {
            changed |= handleEntityMetadata(cachedEntity, entityData, settings);
        }

        if (changed) {
            event.markForReEncode(true);
        }
    }

    private boolean shouldIgnoreEntity(int entityId, CachedEntity cachedEntity, Settings settings) {
        final EntityType entityType = cachedEntity.getEntityType();

        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) {
            return true;
        }

        if (entityType == EntityTypes.WOLF) {
            return true;
        }

        if (settings.getEntityData().isPlayersOnly() && entityType != EntityTypes.PLAYER) {
            return true;
        }

        return !settings.getEntityData().isPlayersOnly() && entityCache.getCurrentVehicleId().map(currentVehicleId -> currentVehicleId == entityId).orElse(false);
    }

    private boolean handleEntityMetadata(CachedEntity cachedEntity, EntityData<?> entityData, Settings settings) {
        final EntityType entityType = cachedEntity.getEntityType();
        boolean changed = updateAirTicks(entityData, settings);

        if (entityType == EntityTypes.IRON_GOLEM) {
            if (!healthTexturesSupported) {
                changed |= applyDefaultSpoofing(entityData, settings);
            } else {
                changed |= spoofIronGolemMetadata(entityData, settings);
            }
        } else {
            changed |= applyDefaultSpoofing(entityData, settings);
            if (entityType == EntityTypes.PLAYER) {
                changed |= spoofPlayerMetadata(entityData, settings);
            }
        }

        return changed;
    }

    private boolean applyDefaultSpoofing(EntityData<?> entityData, Settings settings) {
        if (entityData.getIndex() != player.metadataIndex.HEALTH) return false;
        if (!settings.getEntityData().isHealth()) return false;

        Object value = entityData.getValue();
        if (value instanceof Float && (Float) value > 0f) {
            return setValue(entityData, HealthUtil.getSpoofValue());
        }
        return false;
    }

    private boolean spoofIronGolemMetadata(EntityData<?> entityData, Settings settings) {
        if (entityData.getIndex() != player.metadataIndex.HEALTH || !settings.getEntityData().isHealth()) return false;

        final Object value = entityData.getValue();
        if (!(value instanceof Float)) return false;

        final float health = (Float) value;
        if (health <= 0f) return false;

        final float maxHealth = 100f;
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

        return setValue(entityData, spoofed);
    }

    private boolean spoofPlayerMetadata(EntityData<?> entityData, Settings settings) {
        boolean changed = false;
        if (entityData.getIndex() == player.metadataIndex.ABSORPTION && settings.getEntityData().isAbsorption()) {
            changed = setDynamicValue(entityData, 0);
        }
        if (entityData.getIndex() == player.metadataIndex.XP && settings.getEntityData().isXp()) {
            changed |= setDynamicValue(entityData, 0);
        }
        return changed;
    }

    private boolean updateAirTicks(EntityData<?> entityData, Settings settings) {
        if (entityData.getIndex() == player.metadataIndex.AIR_TICKS && settings.getEntityData().isAirTicks()) {
            return setDynamicValue(entityData, 1);
        }
        return false;
    }

    private boolean setDynamicValue(EntityData<?> entityData, int spoofValue) {
        Object value = entityData.getValue();

        if (value instanceof Integer) {
            return setValue(entityData, spoofValue);
        } else if (value instanceof Short) {
            return setValue(entityData, (short) spoofValue);
        } else if (value instanceof Byte) {
            return setValue(entityData, (byte) spoofValue);
        } else if (value instanceof Long) {
            return setValue(entityData, (long) spoofValue);
        } else if (value instanceof Float) {
            return setValue(entityData, (float) spoofValue);
        } else if (value instanceof Double) {
            return setValue(entityData, (double) spoofValue);
        }
        return false;
    }
}
