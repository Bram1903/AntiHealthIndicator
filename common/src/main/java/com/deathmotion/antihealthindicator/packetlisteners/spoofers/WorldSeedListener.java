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
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;

/**
 * Listens for WorldSeed events to modify the seed value.
 *
 * @param <P> The platform type.
 */
public class WorldSeedListener<P> extends PacketListenerAbstract {

    private final AHIPlatform<P> platform;
    private final ConfigManager<P> settings;

    /**
     * Constructs a new WorldSeedListener with the specified {@link AHIPlatform}.
     *
     * @param platform The platform to use.
     */
    public WorldSeedListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.settings = platform.getConfigManager();

        platform.getLogManager().debug("World Seed listener has been set up.");
    }

    /**
     * This function is called when an {@link PacketSendEvent} is triggered.
     * Overwrites the {@link WrapperPlayServerJoinGame} and {@link WrapperPlayServerRespawn} packets
     * to control seed value.
     *
     * @param event The event that has been triggered.
     */
    @Override
    public void onPacketSend(PacketSendEvent event) {
        final Settings settings = this.settings.getSettings();
        if (!settings.isWorldSeed()) return;

        if (event.getPacketType().equals(PacketType.Play.Server.JOIN_GAME)) {
            if (settings.isAllowBypass()) {
                if (platform.hasPermission(event.getUser().getUUID(), "AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerJoinGame wrapper = new WrapperPlayServerJoinGame(event);
            wrapper.setHashedSeed(0L);
            event.markForReEncode(true);
        }
        if (event.getPacketType().equals(PacketType.Play.Server.RESPAWN)) {
            if (settings.isAllowBypass()) {
                if (platform.hasPermission(event.getUser().getUUID(), "AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerRespawn wrapper = new WrapperPlayServerRespawn(event);
            wrapper.setHashedSeed(0L);
            event.markForReEncode(true);
        }
    }
}