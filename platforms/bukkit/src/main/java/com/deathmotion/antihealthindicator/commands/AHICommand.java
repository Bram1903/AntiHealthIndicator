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

import com.github.retrooper.packetevents.PacketEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class AHICommand implements CommandExecutor {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-ORX]|\\\u25cf");
    private final JavaPlugin plugin;
    private Component pcComponent;

    public AHICommand(JavaPlugin plugin) {
        this.plugin = plugin;
        initPcComponent();
    }

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
                    .findFirst().ifPresent(user -> user.sendMessage(pcComponent));

            return true;
        }

        sender.sendMessage(STRIP_COLOR_PATTERN
                .matcher(LegacyComponentSerializer.legacyAmpersand().serialize(pcComponent))
                .replaceAll("")
                .trim());

        return true;
    }

    private void initPcComponent() {
        pcComponent = Component.text()
                .append(Component.text("\u25cf", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" Running ", NamedTextColor.GRAY))
                .append(Component.text("AntiHealthIndicator", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" v" + plugin.getDescription().getVersion(), NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" by ", NamedTextColor.GRAY))
                .append(Component.text("Bram", NamedTextColor.GREEN))
                .hoverEvent(HoverEvent.showText(Component.text("Open Github Page!", NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)))
                .clickEvent(ClickEvent.openUrl("https://github.com/Bram1903/AntiHealthIndicator"))
                .build();
    }
}