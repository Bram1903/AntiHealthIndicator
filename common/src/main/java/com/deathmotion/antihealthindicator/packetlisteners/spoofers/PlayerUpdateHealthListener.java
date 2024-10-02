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
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;

/**
 * Listens for PlayerUpdateHealth events to modify the health display.
 *
 * @param <P> The platform type.
 */
public class PlayerUpdateHealthListener<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final ConfigManager<P> configManager;

    /**
     * Constructs a new PlayerUpdateHealthListener with the specified {@link AHIPlatform}.
     *
     * @param platform The platform to use.
     */
    public PlayerUpdateHealthListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.configManager = platform.getConfigManager();

        platform.getLogManager().debug("Player Update Health listener has been set up.");
    }

    /**
     * This function is called when an {@link PacketSendEvent} is triggered.
     * Overwrites the {@link WrapperPlayServerUpdateHealth} for players to control how they are displayed.
     *
     * @param event The event that has been triggered.
     */
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.UPDATE_HEALTH) return;

        final Settings settings = configManager.getSettings();
        if (!settings.isFoodSaturation()) return;

        if (settings.isAllowBypass()) {
            if (platform.hasPermission(event.getUser().getUUID(), "AntiHealthIndicator.Bypass")) return;
        }

        WrapperPlayServerUpdateHealth packet = new WrapperPlayServerUpdateHealth(event);

        if (packet.getFoodSaturation() > 0) {
            packet.setFoodSaturation(Float.NaN);
            event.markForReEncode(true);
        }
    }
}
