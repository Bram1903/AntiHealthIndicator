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

package com.deathmotion.antihealthindicator.cache.entities;

import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.github.retrooper.packetevents.protocol.attribute.Attribute;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import lombok.Getter;

import java.util.List;

import static com.deathmotion.antihealthindicator.util.AttributeConstants.MAX_HEALTH_KEY;

@Getter
public class CachedEntity {
    private final EntityType entityType;

    private float health;
    private float maxHealth;

    public CachedEntity(EntityType entityType) {
        this.entityType = entityType;
    }

    public void processMetaData(List<EntityData<?>> entityDataList, AHIPlayer player) {
        for (EntityData<?> entityData : entityDataList) {
            if (entityData.getIndex() == player.metadataIndex.HEALTH) {
                health = (float) entityData.getValue();
                return;
            }
        }
    }

    public void processAttributes(List<WrapperPlayServerUpdateAttributes.Property> properties) {
        for (WrapperPlayServerUpdateAttributes.Property property : properties) {
            final Attribute attribute = property.getAttribute();
            final String attributeName = attribute.getName().getKey();

            if (!attributeName.equals(MAX_HEALTH_KEY)) continue;
            maxHealth = (float) property.calcValue();
        }
    }
}