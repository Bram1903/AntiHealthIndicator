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

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.cache.EntityCache;
import com.deathmotion.antihealthindicator.cache.entities.CachedEntity;
import com.deathmotion.antihealthindicator.cache.entities.WolfEntity;
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.deathmotion.antihealthindicator.models.Settings;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.attribute.Attribute;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;

import static com.deathmotion.antihealthindicator.util.AttributeConstants.MAX_ABSORPTION_KEY;
import static com.deathmotion.antihealthindicator.util.AttributeConstants.MAX_HEALTH_KEY;

public final class AttributeSpoofer extends Spoofer {

    private final EntityCache entityCache;
    private final boolean healthTexturesSupported;

    public AttributeSpoofer(AHIPlayer player) {
        super(player);
        this.entityCache = player.entityCache;
        this.healthTexturesSupported = player.user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.UPDATE_ATTRIBUTES) return;

        Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isHealth()) return;

        WrapperPlayServerUpdateAttributes packet = new WrapperPlayServerUpdateAttributes(event);
        int entityId = packet.getEntityId();

        // Skip processing if the packet refers to the user's own entity.
        if (entityId == player.user.getEntityId()) return;

        final CachedEntity cachedEntity = entityCache.getEntity(entityId);
        if (cachedEntity == null) return;

        final EntityType entityType = cachedEntity.getEntityType();
        if (settings.getEntityData().isPlayersOnly() && entityType != EntityTypes.PLAYER) {
            return;
        }

        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) {
            return;
        }

        if (entityType == EntityTypes.IRON_GOLEM) {
            return;
        }

        if (!settings.getEntityData().isPlayersOnly() && entityCache.getCurrentVehicleId().map(currentVehicleId -> currentVehicleId == entityId).orElse(false)) {
            return;
        }

        if (entityType == EntityTypes.WOLF) {
            WolfEntity wolfEntity = (WolfEntity) cachedEntity;
            if (wolfEntity.shouldIgnoreWolf(player.uuid, settings)) return;
        }

        for (WrapperPlayServerUpdateAttributes.Property property : packet.getProperties()) {
            final Attribute attribute = property.getAttribute();
            final String attributeName = attribute.getName().getKey();

            if (attributeName.equals(MAX_HEALTH_KEY)) {
                property.setValue(1f);
                event.markForReEncode(true);
            } else if (attributeName.equals(MAX_ABSORPTION_KEY) && settings.getEntityData().isAbsorption()) {
                property.setValue(0f);
                event.markForReEncode(true);
            }
        }
    }
}
