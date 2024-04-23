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

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

/**
 * This class holds the indexes of different entity metadata elements like health, absorption and air ticks,
 * the values of which might vary based on the server version.
 */
public class MetadataIndex {

    public static final int AIR_TICKS;

    public static final int HEALTH;

    public static final int ABSORPTION;

    public static final int XP;

    public static final int TAMABLE_TAMED;

    public static final int TAMABLE_OWNER;

    static {
        ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();

        AIR_TICKS = 1;

        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
            HEALTH = 9;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
            HEALTH = 8;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10)) {
            HEALTH = 7;
        } else {
            HEALTH = 6;
        }

        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
            ABSORPTION = 15;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
            ABSORPTION = 14;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
            ABSORPTION = 13;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10)) {
            ABSORPTION = 11;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9)) {
            ABSORPTION = 10;
        } else {
            ABSORPTION = 17;
        }

        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
            XP = 16;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
            XP = 15;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
            XP = 14;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_10)) {
            XP = 12;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9)) {
            XP = 11;
        } else {
            XP = 18;
        }

        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
            TAMABLE_TAMED = 17;
            TAMABLE_OWNER = 18;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
            TAMABLE_TAMED = 16;
            TAMABLE_OWNER = 17;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
            TAMABLE_TAMED = 15;
            TAMABLE_OWNER = 16;
        } else if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_12)) {
            TAMABLE_TAMED = 13;
            TAMABLE_OWNER = 14;
        } else {
            TAMABLE_TAMED = 16;
            TAMABLE_OWNER = 17;
        }
    }
}