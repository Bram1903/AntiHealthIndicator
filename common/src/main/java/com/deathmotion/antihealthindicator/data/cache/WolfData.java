/*
 *
 *  * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  * Copyright (C) 2024 Bram and contributors
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.deathmotion.antihealthindicator.data.cache;

import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class WolfData extends LivingEntityData {
    private boolean isTamed;
    private UUID ownerUUID;

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
    public void processMetaData(EntityData metaData, User user) {
        int index = metaData.getIndex();

        if (index == MetadataIndex.TAMABLE_TAMED) {
            setTamed(((Byte) metaData.getValue() & 0x04) != 0);
        } else if (index == MetadataIndex.TAMABLE_OWNER) {
            Object value = metaData.getValue();

            UUID ownerUUID = value instanceof String
                    ? Optional.ofNullable((String) value)
                    .filter(user.getUUID().toString()::equals)
                    .map(UUID::fromString)
                    .orElse(null)
                    : ((Optional<UUID>) value)
                    .filter(user.getUUID()::equals)
                    .orElse(null);

            setOwnerUUID(ownerUUID);
        }
    }
}
