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

package com.deathmotion.antihealthindicator.util;

import com.deathmotion.antihealthindicator.api.versioning.AHIVersion;
import com.deathmotion.antihealthindicator.models.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CommandComponentCreator {

    private static Component createColoredText(String text, NamedTextColor color, boolean bold) {
        return Component.text(text, color).decoration(TextDecoration.BOLD, bold);
    }

    public static Component createAHICommandComponent() {
        AHIVersion currentVersion = AHIVersions.CURRENT;
        boolean isSnapshot = currentVersion.snapshot();
        String versionString = currentVersion.toStringWithoutSnapshot();

        // Include snapshot commit if applicable
        if (isSnapshot && currentVersion.snapshotCommit() != null) {
            versionString += " (git: " + currentVersion.snapshotCommit() + ")";
        }

        return Component.text()
                .append(createColoredText("\u25cf", NamedTextColor.GREEN, true))
                .append(createColoredText(" Running ", NamedTextColor.GRAY, false))
                .append(createColoredText("AntiHealthIndicator", NamedTextColor.GREEN, true))
                .append(createColoredText(" v" + versionString, NamedTextColor.GREEN, true))
                .append(createColoredText(" by ", NamedTextColor.GRAY, false))
                .append(createColoredText("Bram", NamedTextColor.GREEN, true))
                .hoverEvent(HoverEvent.showText(createColoredText("Open Github Page!", NamedTextColor.GREEN, true)
                        .decorate(TextDecoration.UNDERLINED)))
                .clickEvent(ClickEvent.openUrl(Constants.GITHUB_URL))
                .build();
    }

}

