package com.deathmotion.antihealthindicator.util;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.schedulers.ServerScheduler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This utility class <code>UpdateChecker</code> is used
 * to check for new updates of the plugin from a GitHub releases page.
 * The comparison of the versions is made
 * by comparing the segments of the versions which are divided by the dot character.
 *
 * @author Bram
 * @version 1.1.0
 */
public class UpdateChecker {

    /**
     * Instance of AntiHealthIndicator for which the new updates need to be checked.
     */
    private final AntiHealthIndicator plugin;

    /**
     * The URL of the GitHub API of the repository.
     */
    private final String GITHUB_API_URL = "https://api.github.com/repos/Bram1903/AntiHealthIndicator/releases/latest";

    /**
     * The URL of the GitHub releases page of the repository.
     */
    private final String GITHUB_RELEASES_URL = "https://github.com/Bram1903/AntiHealthIndicator/releases/latest";

    /**
     * Constructor method for UpdateChecker class.
     *
     * @param plugin The instance of JavaPlugin for which updates need to be checked.
     */
    public UpdateChecker(AntiHealthIndicator plugin) {
        this.plugin = plugin;
    }

    /**
     * Method, which checks if new updates of the plugin are available.
     * The updates are checked from the GitHub releases page of a repository.
     */
    public void checkForUpdate() {
        ServerScheduler scheduler = AntiHealthIndicator.getInstance().getScheduler();

        scheduler.runTaskAsynchronously(() -> {
            try {
                List<Integer> parseVersion = Arrays.stream(this.plugin
                                .getDescription()
                                .getVersion()
                                .split("\\."))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                URL api = new URL(GITHUB_API_URL);
                URLConnection con = api.openConnection();

                con.setConnectTimeout(15000);
                con.setReadTimeout(15000);

                JsonObject json = JsonParser.parseReader(new InputStreamReader(con.getInputStream())).getAsJsonObject();

                // getting the version number from the tag name of the latest GitHub release
                List<Integer> parsedTagName = Arrays.stream(json.get("tag_name")
                                .getAsString()
                                .replaceFirst("^[vV]", "")
                                .split("\\."))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                boolean isNewVersionAvailable = false;
                int length = Math.max(parsedTagName.size(), parseVersion.size());

                for (int i = 0; i < length; i++) {
                    int currentVersionPart = i < parseVersion.size() ? parseVersion.get(i) : 0;
                    int latestVersionPart = i < parsedTagName.size() ? parsedTagName.get(i) : 0;

                    if (latestVersionPart > currentVersionPart) {
                        isNewVersionAvailable = true;
                        break;
                    } else if (currentVersionPart > latestVersionPart) {
                        break;
                    }
                }

                if (isNewVersionAvailable) {
                    this.plugin.setUpdateAvailable(true);
                    this.plugin.setLatestVersion(json.get("tag_name").getAsString());

                    if (this.plugin.getConfig().getBoolean("update-checker.print-to-console", true)) {
                        this.plugin.getLogger().info("Found a new version " + json.get("tag_name").getAsString());
                        this.plugin.getLogger().info(GITHUB_RELEASES_URL);
                    }
                }
            } catch (IOException e) {
                LogUpdateError(e);
            }
        });
    }

    /**
     * Method to log the error if checking for new update fails.
     *
     * @param e An instance of IOException representing the occurred error.
     */
    private void LogUpdateError(IOException e) {
        this.plugin.getLogger().severe("<--------------------------------------------------------------->");
        this.plugin.getLogger().severe("Failed to check for a new release!");
        this.plugin.getLogger().severe("Error message:\n" + e.getMessage());
        this.plugin.getLogger().severe("");
        this.plugin.getLogger().severe("Please check if any updates are available.");
        this.plugin.getLogger().severe(GITHUB_RELEASES_URL);
        this.plugin.getLogger().severe("<--------------------------------------------------------------->");
    }
}