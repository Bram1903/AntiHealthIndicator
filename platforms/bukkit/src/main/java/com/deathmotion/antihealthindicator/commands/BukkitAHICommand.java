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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BukkitAHICommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            handleCommand(sender,
                    CommandComponentCreator::createAHICommandComponent,
                    () -> CommandComponentCreator.createLegacyMessage(CommandComponentCreator.createAHICommandComponent()));
            return true;
        } else if ("help".equalsIgnoreCase(args[0])) {
            handleCommand(sender,
                    CommandComponentCreator::createHelpComponent,
                    () -> CommandComponentCreator.createLegacyMessage(CommandComponentCreator.createHelpComponent()));
            return true;
        } else if ("discord".equalsIgnoreCase(args[0])) {
            handleCommand(sender,
                    CommandComponentCreator::createDiscordComponent,
                    CommandComponentCreator::createLegacyDiscordMessage);
            return true;
        }

        handleCommand(sender,
                CommandComponentCreator::createUnknownSubcommandComponent,
                () -> CommandComponentCreator.createLegacyMessage(CommandComponentCreator.createUnknownSubcommandComponent()));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Constants.SUB_COMMANDS
                    .stream()
                    .map(SubCommand::getName)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private void handleCommand(CommandSender sender, Supplier<Component> componentSupplier, Supplier<String> legacyMessageSupplier) {
        if (sender instanceof Player) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(sender);
            user.sendMessage(componentSupplier.get());
        } else {
            sender.sendMessage(legacyMessageSupplier.get());
        }
    }
}

