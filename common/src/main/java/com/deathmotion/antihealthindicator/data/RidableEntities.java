/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.data;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class representing entities that are ridable.
 * This class provides a constant list of the entity types that are ridable.
 */
public class RidableEntities {
    public static final List<EntityType> RIDABLE_ENTITY_TYPES;

    static {
        RIDABLE_ENTITY_TYPES = Collections.unmodifiableList(Arrays.asList(
                EntityTypes.CAMEL,
                EntityTypes.CHESTED_HORSE,
                EntityTypes.DONKEY,
                EntityTypes.HORSE,
                EntityTypes.MULE,
                EntityTypes.PIG,
                EntityTypes.SKELETON_HORSE,
                EntityTypes.STRIDER,
                EntityTypes.ZOMBIE_HORSE
        ));
    }
}