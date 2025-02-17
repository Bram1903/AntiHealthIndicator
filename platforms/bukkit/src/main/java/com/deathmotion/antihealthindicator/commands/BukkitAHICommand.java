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

import com.deathmotion.antihealthindicator.AHIBukkit;
import com.deathmotion.antihealthindicator.data.CommonUser;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BukkitAHICommand implements CommandExecutor, TabExecutor {

    private final AHIBukkit plugin;

    public BukkitAHICommand(AHIBukkit plugin) {
        this.plugin = plugin;
        plugin.getCommand("antihealthindicator").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        plugin.getAhi().getCommand().onCommand(createCommonUser(sender), args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return plugin.getAhi().getCommand().onTabComplete(createCommonUser(sender), args);
    }

    private CommonUser<JavaPlugin> createCommonUser(CommandSender sender) {
        if (sender instanceof Player) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(sender);
            return new CommonUser<>(plugin.getAhi(), user);
        } else {
            return new CommonUser<>(plugin.getAhi(), null);
        }
    }
}

