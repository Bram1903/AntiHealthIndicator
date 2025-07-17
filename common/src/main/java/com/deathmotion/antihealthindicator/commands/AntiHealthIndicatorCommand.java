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

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.commands.antihealthindicator.InfoCommand;
import com.deathmotion.antihealthindicator.commands.antihealthindicator.ReloadCommand;
import com.deathmotion.antihealthindicator.models.CommonUser;
import com.deathmotion.antihealthindicator.util.CommandComponentCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AntiHealthIndicatorCommand<P> {
    private final AHIPlatform<P> platform;

    private final Component versionComponent;
    private final Map<String, SubCommand<P>> subCommands = new HashMap<>();

    public AntiHealthIndicatorCommand(AHIPlatform<P> platform) {
        this.platform = platform;

        subCommands.put("info", new InfoCommand<>());
        subCommands.put("reload", new ReloadCommand<>(platform));

        versionComponent = CommandComponentCreator.createAHICommandComponent();
    }

    public void onCommand(@NotNull CommonUser<P> sender, @NotNull String[] args) {
        platform.getScheduler().runAsyncTask((o) -> {
            if (!hasAnyPermission(sender)) {
                sender.sendMessage(versionComponent);
                return;
            }

            if (args.length == 0) {
                sender.sendMessage(getAvailableCommandsComponent(sender));
                return;
            }

            String subCommandName = args[0].toLowerCase();
            SubCommand<P> subCommand = subCommands.get(subCommandName);

            if (subCommand != null && hasPermissionForSubCommand(sender, subCommandName)) {
                subCommand.execute(sender, args);
            } else {
                sender.sendMessage(getAvailableCommandsComponent(sender));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<String> onTabComplete(@NotNull CommonUser<P> sender, @NotNull String[] args) {
        try {
            return (List<String>) CompletableFuture.supplyAsync(() -> {
                if (args.length == 1) {
                    return subCommands.keySet().stream()
                            .filter(name -> name.startsWith(args[0].toLowerCase()))
                            .filter(name -> hasPermissionForSubCommand(sender, name))
                            .collect(Collectors.toList());
                } else if (args.length > 1) {
                    SubCommand<P> subCommand = subCommands.get(args[0].toLowerCase());
                    if (subCommand != null && hasPermissionForSubCommand(sender, args[0].toLowerCase())) {
                        return subCommand.onTabComplete(sender, args);
                    }
                }
                return Collections.emptyList();
            }).get();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private boolean hasAnyPermission(CommonUser<P> sender) {
        return subCommands.keySet().stream()
                .anyMatch(command -> !command.equals("info") && hasPermissionForSubCommand(sender, command));
    }

    private boolean hasPermissionForSubCommand(CommonUser<P> sender, String subCommand) {
        switch (subCommand) {
            case "info":
                return true;
            case "reload":
                return sender.hasPermission("AntiHealthIndicator.Reload");
            default:
                return false;
        }
    }

    private Component getAvailableCommandsComponent(CommonUser<P> sender) {
        // Start building the help message
        TextComponent.Builder componentBuilder = Component.text()
                .append(Component.text("AntiHealthIndicator Commands", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("Below are the available subcommands:", NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.newline());

        Map<String, String> commandDescriptions = new HashMap<>();
        commandDescriptions.put("info", "Show plugin information.");
        commandDescriptions.put("reload", "Reload the plugin configuration.");

        // Add each command to the message if the sender has permission
        for (String command : subCommands.keySet()) {
            if (hasPermissionForSubCommand(sender, command)) {
                componentBuilder.append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(Component.text("/ahi " + command, NamedTextColor.GOLD, TextDecoration.BOLD))
                        .append(Component.text(" - ", NamedTextColor.GRAY))
                        .append(Component.text(commandDescriptions.get(command), NamedTextColor.GRAY))
                        .append(Component.newline());
            }
        }

        // If no commands are available, provide a different message
        if (componentBuilder.children().isEmpty()) {
            return Component.text("You do not have permission to use any commands.", NamedTextColor.RED);
        }

        return componentBuilder.build();
    }
}
