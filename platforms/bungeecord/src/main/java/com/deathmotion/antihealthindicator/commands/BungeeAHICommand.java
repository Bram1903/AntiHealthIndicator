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
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BungeeAHICommand extends Command implements TabExecutor {

    public BungeeAHICommand(Plugin plugin) {
        super("AntiHealthIndicator", null, "ahi");
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    private void handleCommand(CommandSender sender, Supplier<Component> componentSupplier, Supplier<String> legacyMessageSupplier) {
        if (sender instanceof ProxiedPlayer) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(sender);
            user.sendMessage(componentSupplier.get());
        } else {
            sender.sendMessage(legacyMessageSupplier.get());
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            handleCommand(sender,
                    CommandComponentCreator::createAHICommandComponent,
                    () -> CommandComponentCreator.createLegacyMessage(CommandComponentCreator.createAHICommandComponent()));
        } else {
            switch (args[0].toLowerCase()) {
                case "help":
                    handleCommand(sender,
                            CommandComponentCreator::createHelpComponent,
                            () -> CommandComponentCreator.createLegacyMessage(CommandComponentCreator.createHelpComponent()));
                    break;
                case "discord":
                    handleCommand(sender,
                            CommandComponentCreator::createDiscordComponent,
                            CommandComponentCreator::createLegacyDiscordMessage);
                    break;
                default:
                    handleCommand(sender,
                            CommandComponentCreator::createUnknownSubcommandComponent,
                            () -> CommandComponentCreator.createLegacyMessage(CommandComponentCreator.createUnknownSubcommandComponent()));
                    break;
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Constants.SUB_COMMANDS.stream()
                    .map(SubCommand::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

