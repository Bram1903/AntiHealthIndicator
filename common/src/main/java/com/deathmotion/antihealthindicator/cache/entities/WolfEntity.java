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
import com.deathmotion.antihealthindicator.models.Settings;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class WolfEntity extends CachedEntity {
    private boolean isTamed;
    private UUID ownerUUID;

    public WolfEntity() {
        super(EntityTypes.WOLF);
    }

    public boolean isOwnerPresent() {
        return ownerUUID != null;
    }

    public UUID getOwnerUUID() {
        if (!isOwnerPresent()) {
            throw new IllegalStateException("Owner UUID not present");
        }
        return ownerUUID;
    }

    @Override
    public void processMetaData(List<EntityData<?>> entityDataList, AHIPlayer player) {
        for (EntityData<?> entityData : entityDataList) {
            int index = entityData.getIndex();

            if (index == player.metadataIndex.TAMABLE_TAMED) {
                isTamed = (((Byte) entityData.getValue() & 0x04) != 0);
            } else if (index == player.metadataIndex.TAMABLE_OWNER) {
                Object value = entityData.getValue();

                ownerUUID = value instanceof String
                        ? Optional.of((String) value)
                        .filter(player.uuid.toString()::equals)
                        .map(UUID::fromString)
                        .orElse(null)
                        : ((Optional<UUID>) value)
                        .filter(player.uuid::equals)
                        .orElse(null);
            }
        }
    }

    public boolean shouldIgnoreWolf(UUID playerUuid, Settings settings) {
        Settings.EntityData.Wolves wolfSettings = settings.getEntityData().getWolves();
        if (wolfSettings.isEnabled()) return true;

        if (!wolfSettings.isTamed() && !wolfSettings.isOwner()) return true;
        if (wolfSettings.isTamed() && isTamed()) return true;
        return wolfSettings.isOwner()
                && isOwnerPresent()
                && getOwnerUUID().equals(playerUuid);
    }
}
