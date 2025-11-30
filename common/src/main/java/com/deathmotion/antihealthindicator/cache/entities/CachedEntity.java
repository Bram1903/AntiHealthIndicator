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
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Getter;

import java.util.List;

public final class CachedEntity {
    @Getter
    private final EntityType entityType;

    @Getter
    private float health;

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
}