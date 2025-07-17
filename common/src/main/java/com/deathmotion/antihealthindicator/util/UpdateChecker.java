/*
 *  This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  Copyright (C) 2025 Bram and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.util;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.api.versioning.AHIVersion;
import com.deathmotion.antihealthindicator.managers.LogManager;
import com.deathmotion.antihealthindicator.models.Constants;
import com.deathmotion.antihealthindicator.models.Settings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker<P> {
    private final AHIPlatform<P> platform;
    private final Settings settings;
    private final LogManager<P> logManager;

    @Getter
    private boolean updateAvailable = false;
    private @Nullable AHIVersion latestVersion;

    public UpdateChecker(AHIPlatform<P> platform) {
        this.platform = platform;
        this.settings = platform.getConfigManager().getSettings();
        this.logManager = platform.getLogManager();

        if (settings.getUpdateChecker().isEnabled()) {
            checkForUpdate();
        }
    }

    public void checkForUpdate() {
        CompletableFuture.runAsync(() -> {
            try {
                AHIVersion localVersion = AHIVersions.CURRENT;
                latestVersion = fetchLatestGitHubVersion();

                if (latestVersion != null) {
                    handleVersionComparison(localVersion, latestVersion);
                } else {
                    logManager.warn("Unable to fetch the latest version from GitHub.");
                }
            } catch (Exception ex) {
                logManager.warn("Failed to check for updates: " + ex.getMessage());
            }
        });
    }

    private AHIVersion fetchLatestGitHubVersion() {
        try {
            URLConnection connection = new URL(Constants.GITHUB_API_URL).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/4.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String jsonResponse = reader.readLine();
            reader.close();
            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
            return AHIVersion.fromString(jsonObject.get("tag_name").getAsString().replaceFirst("^[vV]", ""));
        } catch (IOException e) {
            logManager.warn("Failed to parse AntiHealthIndicator version! Version API: " + e.getMessage());
            return null;
        }
    }

    private void handleVersionComparison(AHIVersion localVersion, AHIVersion latestVersion) {
        if (localVersion.isOlderThan(latestVersion)) {
            notifyUpdateAvailable(localVersion, latestVersion);
        } else if (localVersion.isNewerThan(latestVersion)) {
            notifyOnDevBuild(localVersion, latestVersion);
        }
    }

    private void notifyUpdateAvailable(AHIVersion currentVersion, AHIVersion newVersion) {
        if (settings.getUpdateChecker().isPrintToConsole()) {
            platform.sendConsoleMessage(Component.text("[AntiHealthIndicator] ", NamedTextColor.DARK_GREEN)
                    .append(Component.text("Update available! ", NamedTextColor.BLUE))
                    .append(Component.text("Current version: ", NamedTextColor.WHITE))
                    .append(Component.text(currentVersion.toStringWithoutSnapshot(), NamedTextColor.GOLD))
                    .append(Component.text(" | New version: ", NamedTextColor.WHITE))
                    .append(Component.text(newVersion.toStringWithoutSnapshot(), NamedTextColor.DARK_PURPLE)));
        }

        updateAvailable = true;
    }

    private void notifyOnDevBuild(AHIVersion currentVersion, AHIVersion newVersion) {
        if (settings.getUpdateChecker().isPrintToConsole()) {
            platform.sendConsoleMessage(Component.text("[AntiHealthIndicator] ", NamedTextColor.DARK_GREEN)
                    .append(Component.text("Development build detected. ", NamedTextColor.WHITE))
                    .append(Component.text("Current version: ", NamedTextColor.WHITE))
                    .append(Component.text(currentVersion.toStringWithoutSnapshot(), NamedTextColor.AQUA))
                    .append(Component.text(" | Latest stable version: ", NamedTextColor.WHITE))
                    .append(Component.text(newVersion.toStringWithoutSnapshot(), NamedTextColor.DARK_AQUA)));
        }
    }

    public Component getUpdateComponent() {
        return Component.text()
                .append(Component.text("[AntiHealthIndicator] ", NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text("Version " + latestVersion.toStringWithoutSnapshot() + " is ", NamedTextColor.GREEN))
                .append(Component.text("now available", NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to download", NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.openUrl(Constants.MODRINTH_URL)))
                .append(Component.text("!", NamedTextColor.GREEN))
                .build();
    }
}