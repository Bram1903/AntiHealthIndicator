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

package com.deathmotion.antihealthindicator.commands;

import com.deathmotion.antihealthindicator.data.Constants;
import com.deathmotion.antihealthindicator.data.SubCommand;
import com.deathmotion.antihealthindicator.util.CommandComponentCreator;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VelocityAHICommand implements SimpleCommand {

    public VelocityAHICommand(ProxyServer proxyServer) {
        CommandMeta commandMeta = proxyServer.getCommandManager().metaBuilder("antihealthindicator")
                .aliases("ahi")
                .build();
        proxyServer.getCommandManager().register(commandMeta, this);
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();

        if (args.length == 0) {
            source.sendMessage(CommandComponentCreator.createAHICommandComponent());
        } else {
            switch (args[0].toLowerCase()) {
                case "help":
                    source.sendMessage(CommandComponentCreator.createHelpComponent());
                    break;
                case "discord":
                    source.sendMessage(CommandComponentCreator.createDiscordComponent());
                    break;
                default:
                    source.sendMessage(CommandComponentCreator.createUnknownSubcommandComponent());
                    break;
            }
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) {
            return Constants.SUB_COMMANDS.stream()
                    .map(SubCommand::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
