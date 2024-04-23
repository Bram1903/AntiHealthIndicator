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

package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.concurrent.TimeUnit;

public class PlayerJoin<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final Component updateComponent;

    public PlayerJoin(AHIPlatform<P> platform, String latestVersion) {
        this.platform = platform;

        this.updateComponent = Component.text()
                .append(Component.text("[AntiHealthIndicator] ", NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text("Version " + latestVersion + " is ", NamedTextColor.GREEN))
                .append(Component.text("now available", NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to download", NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.openUrl("https://www.spigotmc.org/resources/antihealthindicator.20803/")))
                .append(Component.text("!", NamedTextColor.GREEN))
                .build();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (PacketType.Play.Server.JOIN_GAME == event.getPacketType()) {
            User user = event.getUser();

            platform.getScheduler().rynAsyncTaskDelayed((o) -> {
                if (platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Notify")) {
                    user.sendMessage(updateComponent);
                }
            }, 2, TimeUnit.SECONDS);
        }
    }
}