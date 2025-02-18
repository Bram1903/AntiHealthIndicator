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

package com.deathmotion.antihealthindicator.packets;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;

import java.util.concurrent.TimeUnit;

public class PacketPlayerJoinQuit<P> extends PacketListenerAbstract {

    private final AHIPlatform<P> platform;

    public PacketPlayerJoinQuit(AHIPlatform<P> platform) {
        this.platform = platform;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
            // Do this after send to avoid sending packets before the PLAY state
            event.getTasksAfterSend().add(() -> platform.getPlayerDataManager().addUser(event.getUser()));
        }
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        if (platform.getConfigManager().getSettings().getUpdateChecker().isNotifyInGame() && platform.getUpdateManager().isUpdateAvailable()) {
            User user = event.getUser();

            if (platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Update")) {
                platform.getScheduler().runAsyncTaskDelayed((o) -> user.sendMessage(platform.getUpdateManager().getUpdateComponent()), 2, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        platform.getPlayerDataManager().remove(event.getUser());
    }
}
