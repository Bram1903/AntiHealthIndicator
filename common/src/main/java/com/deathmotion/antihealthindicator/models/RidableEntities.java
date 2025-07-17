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

package com.deathmotion.antihealthindicator.models;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RidableEntities {
    private static final Set<EntityType> RIDABLE_ENTITY_TYPES;

    static {
        RIDABLE_ENTITY_TYPES = new HashSet<>(Arrays.asList(
                EntityTypes.CAMEL,
                EntityTypes.CHESTED_HORSE,
                EntityTypes.DONKEY,
                EntityTypes.HORSE,
                EntityTypes.MULE,
                EntityTypes.PIG,
                EntityTypes.SKELETON_HORSE,
                EntityTypes.STRIDER,
                EntityTypes.ZOMBIE_HORSE,
                EntityTypes.LLAMA,
                EntityTypes.TRADER_LLAMA,
                EntityTypes.HAPPY_GHAST
        ));
    }

    public static boolean isRideable(EntityType entityType) {
        return RIDABLE_ENTITY_TYPES.contains(entityType);
    }
}