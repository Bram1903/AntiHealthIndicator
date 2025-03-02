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

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.player.User;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager<P> {

    private final AHIPlatform<P> platform;
    private final ConcurrentHashMap<User, AHIPlayer> playerDataMap = new ConcurrentHashMap<>();

    public PlayerDataManager(AHIPlatform<P> platform) {
        this.platform = platform;
    }

    public boolean shouldCheck(User user) {
        if (!ChannelHelper.isOpen(user.getChannel())) return false;
        if (user.getUUID() == null) return false;

        return !platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass");
    }

    @Nullable
    public AHIPlayer getPlayer(final User user) {
        return playerDataMap.get(user);
    }

    public void sendGlobalPacket(PacketSendEvent event) {
        for (AHIPlayer player : playerDataMap.values()) {
            player.entityCache.onGlobalPacketSend(event);
        }
    }

    public void addUser(final User user) {
        if (shouldCheck(user)) {
            playerDataMap.put(user, new AHIPlayer(user));
        }
    }

    public void remove(final User player) {
        playerDataMap.remove(player);
    }
}
