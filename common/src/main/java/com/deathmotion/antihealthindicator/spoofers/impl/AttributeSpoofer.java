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
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.attribute.Attribute;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;

import static com.deathmotion.antihealthindicator.util.AttributeConstants.MAX_ABSORPTION_KEY;
import static com.deathmotion.antihealthindicator.util.AttributeConstants.MAX_HEALTH_KEY;

public final class AttributeSpoofer extends Spoofer {

    private final EntityCache entityCache;

    public AttributeSpoofer(AHIPlayer player) {
        super(player);
        entityCache = player.entityCache;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.UPDATE_ATTRIBUTES) return;

        Settings settings = configManager.getSettings();
        if (!settings.isAttributes()) return;

        WrapperPlayServerUpdateAttributes packet = new WrapperPlayServerUpdateAttributes(event);
        int entityId = packet.getEntityId();

        // Skip processing if the packet refers to the user's own entity.
        if (entityId == player.user.getEntityId()) return;

        final CachedEntity cachedEntity = entityCache.getEntity(entityId);
        if (cachedEntity == null) return;

        final EntityType entityType = cachedEntity.getEntityType();
        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) {
            return;
        }

        // TODO: FIX Vehicles showing the wrong health due to this (I think)
        // TODO: FIX Golems not showing the right health texture assuming the texture is based on percentages (I think)

        for (WrapperPlayServerUpdateAttributes.Property property : packet.getProperties()) {
            final Attribute attribute = property.getAttribute();
            final String attributeName = attribute.getName().getKey();

            if (attributeName.equals(MAX_HEALTH_KEY)) {
                //AHIPlatform.getInstance().getLogManager().info("Spoofing max_health from " + property.getValue() + " to " + attribute.getDefaultValue() + " for entity " + entityId);
                property.setValue(attribute.getDefaultValue());
                event.markForReEncode(true);
            } else if (attributeName.equals(MAX_ABSORPTION_KEY)) {
                //AHIPlatform.getInstance().getLogManager().info("Spoofing max_absorption from " + property.getValue() + " to " + attribute.getDefaultValue() + " for entity " + entityId);
                property.setValue(attribute.getDefaultValue());
                event.markForReEncode(true);
            }
        }
    }
}
