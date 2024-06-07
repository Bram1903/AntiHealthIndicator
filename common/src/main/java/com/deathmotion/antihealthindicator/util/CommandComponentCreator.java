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

package com.deathmotion.antihealthindicator.util;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.Constants;
import com.deathmotion.antihealthindicator.data.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

public class CommandComponentCreator {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-ORX]|\\u25cf");
    private static String pluginVersion;

    private static String getPluginVersion() {
        if (pluginVersion == null) {
            pluginVersion = AHIPlatform.class.getPackage().getImplementationVersion();
        }
        return pluginVersion;
    }

    private static Component createColoredText(String text, NamedTextColor color, boolean bold) {
        return Component.text(text, color).decoration(TextDecoration.BOLD, bold);
    }

    public static Component createAHICommandComponent() {
        return Component.text()
                .append(createColoredText("\u25cf", NamedTextColor.GREEN, true))
                .append(createColoredText(" Running ", NamedTextColor.GRAY, false))
                .append(createColoredText("AntiHealthIndicator", NamedTextColor.GREEN, true))
                .append(createColoredText(" v" + getPluginVersion(), NamedTextColor.GREEN, true))
                .append(createColoredText(" by ", NamedTextColor.GRAY, false))
                .append(createColoredText("Bram", NamedTextColor.GREEN, true))
                .hoverEvent(HoverEvent.showText(createColoredText("Open Github Page!", NamedTextColor.GREEN, true)
                        .decorate(TextDecoration.UNDERLINED)))
                .clickEvent(ClickEvent.openUrl(Constants.GITHUB_URL))
                .build();
    }

    public static Component createHelpComponent() {
        Component baseComponent = Component.text()
                .append(createColoredText("\u25cf ", NamedTextColor.BLUE, true))
                .append(createColoredText("AntiHealthIndicator Help", NamedTextColor.BLUE, true))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Main Command: ", NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.text(" - ")
                        .append(createColoredText("/ahi", NamedTextColor.AQUA, true))
                        .append(Component.text(" : Main Command (Shows plugin version)")))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Sub Commands: ", NamedTextColor.GRAY))
                .append(Component.newline())
                .build();

        for (SubCommand subCommand : Constants.SUB_COMMANDS) {
            baseComponent = baseComponent.append(createSubCommandComponent("/" + subCommand.getName(), subCommand.getDescription()));
        }

        return baseComponent;
    }

    private static Component createSubCommandComponent(String command, String description) {
        return Component.text(" - ")
                .append(createColoredText(command, NamedTextColor.AQUA, true))
                .append(Component.text(" : " + description))
                .append(Component.newline());
    }

    public static Component createDiscordComponent() {
        return Component.text()
                .append(createColoredText("\u25cf", NamedTextColor.GREEN, true))
                .append(createColoredText(" Join the ", NamedTextColor.GRAY, false))
                .append(createColoredText("AntiHealthIndicator Discord", NamedTextColor.GREEN, true)
                        .decorate(TextDecoration.UNDERLINED))
                .append(createColoredText(" server!", NamedTextColor.GRAY, false))
                .hoverEvent(HoverEvent.showText(createColoredText("Join Discord Server!", NamedTextColor.GREEN, true)
                        .decorate(TextDecoration.UNDERLINED)))
                .clickEvent(ClickEvent.openUrl(Constants.DISCORD_URL))
                .build();
    }

    public static Component createUnknownSubcommandComponent() {
        return Component.text()
                .append(createColoredText("\u25cf", NamedTextColor.RED, true))
                .append(createColoredText(" Unknown subcommand! Use ", NamedTextColor.GRAY, false))
                .append(createColoredText("/ahi help", NamedTextColor.RED, true))
                .append(createColoredText(" for a list of sub commands.", NamedTextColor.GRAY, false))
                .build();
    }

    public static String createLegacyMessage(Component component) {
        return STRIP_COLOR_PATTERN.matcher(LegacyComponentSerializer.legacyAmpersand().serialize(component)).replaceAll("").trim();
    }

    public static String createLegacyDiscordMessage() {
        return "Join the AntiHealthIndicator Discord server! Link: " + Constants.DISCORD_URL;
    }
}

