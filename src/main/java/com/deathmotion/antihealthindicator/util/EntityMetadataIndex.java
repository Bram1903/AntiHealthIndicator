package com.deathmotion.antihealthindicator.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

/**
 * This class holds the indexes of different entity metadata elements like health, absorption and air ticks,
 * the values of which might vary based on the server version.
 */
public class EntityMetadataIndex {

    public static final int AIR_TICKS;

    public static final int HEALTH;

    public static final int ABSORPTION;

    public static final int XP;

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
    }
}