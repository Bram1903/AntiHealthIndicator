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

package com.deathmotion.antihealthindicator.spoofers.impl;

import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.deathmotion.antihealthindicator.spoofers.type.PacketSpoofer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;

public class WorldSeedSpoofer extends Spoofer implements PacketSpoofer {

    public WorldSeedSpoofer(AHIPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        final Settings settings = configManager.getSettings();
        if (!settings.isWorldSeed()) return;

        if (event.getPacketType().equals(PacketType.Play.Server.JOIN_GAME)) {
            WrapperPlayServerJoinGame wrapper = new WrapperPlayServerJoinGame(event);
            wrapper.setHashedSeed(0L);
            event.markForReEncode(true);
        }
        if (event.getPacketType().equals(PacketType.Play.Server.RESPAWN)) {
            WrapperPlayServerRespawn wrapper = new WrapperPlayServerRespawn(event);
            wrapper.setHashedSeed(0L);
            event.markForReEncode(true);
        }
    }
}
