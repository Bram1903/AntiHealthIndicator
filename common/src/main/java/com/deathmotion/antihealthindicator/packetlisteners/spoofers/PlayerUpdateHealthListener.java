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

package com.deathmotion.antihealthindicator.packetlisteners.spoofers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;

public class PlayerUpdateHealthListener<P> extends PacketListenerAbstract {


    private final AHIPlatform<P> platform;
    private final boolean bypassPermissionEnabled;

    public PlayerUpdateHealthListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.bypassPermissionEnabled = platform.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_HEALTH) {
            if (bypassPermissionEnabled) {
                if (platform.hasPermission(event.getUser().getUUID(), "AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerUpdateHealth packet = new WrapperPlayServerUpdateHealth(event);

            if (packet.getFoodSaturation() > 0) {
                packet.setFoodSaturation(Float.NaN);
                event.markForReEncode(true);
            }
        }
    }
}
