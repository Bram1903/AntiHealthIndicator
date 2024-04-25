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

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.packetlisteners.PlayerJoin;
import com.github.retrooper.packetevents.PacketEvents;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateManager<P> {
    private final static String GITHUB_API_URL = "https://api.github.com/repos/Bram1903/AntiHealthIndicator/releases/latest";
    private final static String GITHUB_RELEASES_URL = "https://github.com/Bram1903/AntiHealthIndicator/releases/latest";

    private final AHIPlatform<P> platform;

    public UpdateManager(AHIPlatform<P> platform) {
        this.platform = platform;

        initializeUpdateCheck();
    }

    private void initializeUpdateCheck() {
        if (isUpdateCheckerEnabled()) {
            checkForUpdate(shouldPrintUpdateToConsole());
        }
    }

    private boolean isUpdateCheckerEnabled() {
        return platform.getConfigurationOption(ConfigOption.UPDATE_CHECKER_ENABLED);
    }

    private boolean shouldPrintUpdateToConsole() {
        return platform.getConfigurationOption(ConfigOption.UPDATE_CHECKER_PRINT_TO_CONSOLE);
    }

    private boolean shouldNotifyInGame() {
        return platform.getConfigurationOption(ConfigOption.NOTIFY_IN_GAME);
    }

    public void checkForUpdate(boolean printToConsole) {
        platform.getScheduler().runAsyncTask((o) -> {
            try {
                List<Integer> currentVersion = parseVersion(platform.getPluginVersion());
                List<Integer> latestVersion = getLatestGitHubVersion();

                compareVersions(currentVersion, latestVersion, printToConsole);
            } catch (IOException e) {
                LogUpdateError(e);
            }
        });
    }

    private List<Integer> parseVersion(String version) {
        return Arrays.stream(version.split("\\."))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private List<Integer> getLatestGitHubVersion() throws IOException {
        URLConnection connection = new URL(GITHUB_API_URL).openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.0");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String jsonResponse = reader.readLine();
        reader.close();
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

        return parseVersion(jsonObject.get("tag_name").getAsString().replaceFirst("^[vV]", ""));
    }

    private void compareVersions(List<Integer> currentVersion, List<Integer> latestVersion, boolean printToConsole) {
        boolean isNewVersionAvailable = false;
        int length = Math.max(latestVersion.size(), currentVersion.size());

        for (int i = 0; i < length; i++) {
            int currentVersionPart = i < currentVersion.size() ? currentVersion.get(i) : 0;
            int latestVersionPart = i < latestVersion.size() ? latestVersion.get(i) : 0;

            if (latestVersionPart > currentVersionPart) {
                isNewVersionAvailable = true;
                break;
            } else if (currentVersionPart > latestVersionPart) {
                break;
            }
        }

        if (isNewVersionAvailable) {
            String formattedVersion = latestVersion.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("."));

            printUpdateInfo(printToConsole, formattedVersion);
        }
    }

    private void printUpdateInfo(boolean printToConsole, String formattedVersion) {
        if (printToConsole) {
            platform.getLogManager().info("Found a new version " + formattedVersion);
            platform.getLogManager().info(GITHUB_RELEASES_URL);
        }

        if (shouldNotifyInGame()) {
            PacketEvents.getAPI().getEventManager().registerListener(new PlayerJoin<>(platform, formattedVersion));
        }
    }

    /**
     * Method to log the error if checking for new update fails.
     *
     * @param e An instance of IOException representing the occurred error.
     */
    private void LogUpdateError(IOException e) {
        platform.getLogManager().error("<--------------------------------------------------------------->");
        platform.getLogManager().error("Failed to check for a new release!");
        platform.getLogManager().error("Error message:\n" + e.getMessage());
        platform.getLogManager().info(GITHUB_RELEASES_URL);
        platform.getLogManager().error("<--------------------------------------------------------------->");
    }
}