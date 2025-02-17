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

package com.deathmotion.antihealthindicator.commands;

import com.deathmotion.antihealthindicator.AHIVelocity;
import com.deathmotion.antihealthindicator.data.CommonUser;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;

public class VelocityAHICommand implements SimpleCommand {

    private final AHIVelocity plugin;

    public VelocityAHICommand(AHIVelocity plugin, ProxyServer server) {
        this.plugin = plugin;

        CommandMeta commandMeta = server.getCommandManager().metaBuilder("antihealthindicator")
                .aliases("ahi")
                .build();
        server.getCommandManager().register(commandMeta, this);
    }

    @Override
    public void execute(Invocation invocation) {
        plugin.getAhi().getCommand().onCommand(createCommonUser(invocation.source()), invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return plugin.getAhi().getCommand().onTabComplete(createCommonUser(invocation.source()), invocation.arguments());
    }

    private CommonUser<ProxyServer> createCommonUser(CommandSource sender) {
        if (sender instanceof Player) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(sender);
            return new CommonUser<>(plugin.getAhi(), user);
        } else {
            return new CommonUser<>(plugin.getAhi(), null);
        }
    }
}
