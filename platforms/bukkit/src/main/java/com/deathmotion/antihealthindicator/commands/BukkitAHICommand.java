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

import com.deathmotion.antihealthindicator.util.ComponentCreator;
import com.github.retrooper.packetevents.PacketEvents;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class BukkitAHICommand implements CommandExecutor {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-ORX]|\\\u25cf");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("AntiHealthIndicator.Version")) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return false;
        }

        if (sender instanceof Player) {
            PacketEvents.getAPI()
                    .getProtocolManager()
                    .getUsers()
                    .stream()
                    .filter(user -> user != null && user.getUUID() != null)
                    .filter(user -> user.getUUID().equals(((Player) sender).getUniqueId()))
                    .findFirst().ifPresent(user -> user.sendMessage(ComponentCreator.createAHICommandComponent()));

            return true;
        }

        sender.sendMessage(STRIP_COLOR_PATTERN
                .matcher(LegacyComponentSerializer.legacyAmpersand().serialize(ComponentCreator.createAHICommandComponent()))
                .replaceAll("")
                .trim());

        return true;
    }
}