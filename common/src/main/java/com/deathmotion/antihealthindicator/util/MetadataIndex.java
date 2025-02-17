/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2025 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
        HEALTH = getHealthIndex(version);
        ABSORPTION = getAbsorptionIndex(version);
        XP = getXPIndex(version);
        TAMABLE_TAMED = getTameIndex(version);
        TAMABLE_OWNER = getOwnerIndex(version);
    }

    private int getHealthIndex(ClientVersion version) {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_17)) {
            return 9;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_14)) {
            return 8;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_10)) {
            return 7;
        } else {
            return 6;
        }
    }

    private int getAbsorptionIndex(ClientVersion version) {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_17)) {
            return 15;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_15)) {
            return 14;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_14)) {
            return 13;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_10)) {
            return 11;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_9)) {
            return 10;
        } else {
            return 17;
        }
    }

    private int getXPIndex(ClientVersion version) {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_17)) {
            return 16;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_15)) {
            return 15;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_14)) {
            return 14;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_10)) {
            return 12;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_9)) {
            return 11;
        } else {
            return 18;
        }
    }

    private int getTameIndex(ClientVersion version) {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_17)) {
            return 17;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_15)) {
            return 16;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_14)) {
            return 15;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_12)) {
            return 13;
        } else {
            return 16;
        }
    }

    private int getOwnerIndex(ClientVersion version) {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_17)) {
            return 18;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_15)) {
            return 17;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_14)) {
            return 16;
        } else if (version.isNewerThanOrEquals(ClientVersion.V_1_12)) {
            return 14;
        } else {
            return 17;
        }
    }
}
