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

package com.deathmotion.antihealthindicator.util;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;

public class MetadataIndex {

    public final int AIR_TICKS;
    public final int HEALTH;
    public final int ABSORPTION;
    public final int XP;
    public final int TAMABLE_TAMED;
    public final int TAMABLE_OWNER;

    public MetadataIndex(ClientVersion version) {
        AIR_TICKS = 1;
        HEALTH = getIndex(version, new int[]{6, 7, 8, 9}, ClientVersion.V_1_17, ClientVersion.V_1_14, ClientVersion.V_1_10);
        ABSORPTION = getIndex(version, new int[]{17, 10, 11, 13, 14, 15}, ClientVersion.V_1_17, ClientVersion.V_1_15, ClientVersion.V_1_14, ClientVersion.V_1_10, ClientVersion.V_1_9);
        XP = getIndex(version, new int[]{18, 11, 12, 14, 15, 16}, ClientVersion.V_1_17, ClientVersion.V_1_15, ClientVersion.V_1_14, ClientVersion.V_1_10, ClientVersion.V_1_9);
        TAMABLE_TAMED = getIndex(version, new int[]{16, 13, 15, 16, 17}, ClientVersion.V_1_17, ClientVersion.V_1_15, ClientVersion.V_1_14, ClientVersion.V_1_12);
        TAMABLE_OWNER = getIndex(version, new int[]{17, 14, 16, 17, 18}, ClientVersion.V_1_17, ClientVersion.V_1_15, ClientVersion.V_1_14, ClientVersion.V_1_12);
    }

    private int getIndex(ClientVersion version, int[] indices, ClientVersion... versions) {
        for (int i = 0; i < versions.length; i++) {
            if (version.isNewerThanOrEquals(versions[i])) {
                return indices[i];
            }
        }
        return indices[0];
    }
}
