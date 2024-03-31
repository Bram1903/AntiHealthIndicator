package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.packetlisteners.PlayerJoin;
import com.github.retrooper.packetevents.PacketEvents;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        FoliaCompatUtil.runTaskAsync(this.plugin, () -> {
            try {
                List<Integer> currentVersion = parseVersion(this.platform.getPluginVersion());
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
        URL api = new URL(GITHUB_API_URL);
        URLConnection con = getConnection(api);

        JsonObject json = JsonParser.parseReader(new InputStreamReader(con.getInputStream())).getAsJsonObject();

        return parseVersion(json.get("tag_name").getAsString().replaceFirst("^[vV]", ""));
    }

    private URLConnection getConnection(URL api) throws IOException {
        URLConnection con = api.openConnection();
        con.setConnectTimeout(15000);
        con.setReadTimeout(15000);
        return con;
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
            platform.getLoggerWrapper().info("Found a new version " + formattedVersion);
            platform.getLoggerWrapper().info(GITHUB_RELEASES_URL);
        }

        if (shouldNotifyInGame()) {
            FoliaCompatUtil.runTask(this.plugin, (Object unused) -> {
                PacketEvents.getAPI().getEventManager().registerListener(new PlayerJoin<>(this.platform, formattedVersion));
            });
        }
    }

    /**
     * Method to log the error if checking for new update fails.
     *
     * @param e An instance of IOException representing the occurred error.
     */
    private void LogUpdateError(IOException e) {
        platform.getLoggerWrapper().error("<--------------------------------------------------------------->");
        platform.getLoggerWrapper().error("Failed to check for a new release!");
        platform.getLoggerWrapper().error("Error message:\n" + e.getMessage());
        platform.getLoggerWrapper().info(GITHUB_RELEASES_URL);
        platform.getLoggerWrapper().error("<--------------------------------------------------------------->");
    }
}