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
import com.deathmotion.antihealthindicator.data.Settings;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class ConfigManager<P> {
    private final AHIPlatform<P> platform;

    @Getter
    private Settings settings;

    public ConfigManager(AHIPlatform<P> platform) {
        this.platform = platform;
        saveDefaultConfiguration();
        loadConfig();
    }

    private void saveDefaultConfiguration() {
        File configFile = new File(platform.getPluginDirectory(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream inputStream = getClass().getResourceAsStream("/config.yml")) {
                if (inputStream != null) {
                    Files.copy(inputStream, configFile.toPath());
                } else {
                    platform.getLogManager().severe("Default configuration file not found in resources!");
                }
            } catch (IOException e) {
                platform.getLogManager().severe("Failed to save default configuration file: " + e.getMessage());
            }
        }
    }

    private void loadConfig() {
        File configFile = new File(platform.getPluginDirectory(), "config.yml");
        if (!configFile.exists()) {
            platform.getLogManager().severe("Config file not found!");
            platform.commonOnDisable();
            return;
        }

        try (InputStream inputStream = Files.newInputStream(configFile.toPath())) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(inputStream);

            Settings settings = new Settings();
            setConfigOptions(yamlData, settings);

            this.settings = settings;
        } catch (IOException e) {
            platform.getLogManager().severe("Failed to load configuration: " + e.getMessage());
            platform.commonOnDisable();
        }
    }

    private void setConfigOptions(Map<String, Object> yamlData, Settings settings) {
        settings.setDebug(getBoolean(yamlData, "debug.enabled", false));
        settings.getUpdateChecker().setEnabled(getBoolean(yamlData, "update-checker.enabled", true));
        settings.getUpdateChecker().setPrintToConsole(getBoolean(yamlData, "update-checker.print-to-console", true));
        settings.getUpdateChecker().setNotifyInGame(getBoolean(yamlData, "update-checker.notify-in-game", true));
        settings.setAllowBypass(getBoolean(yamlData, "allow-bypass.enabled", false));
        settings.setWorldSeed(getBoolean(yamlData, "spoof.world-seed.enabled", false));
        settings.setFoodSaturation(getBoolean(yamlData, "spoof.food-saturation.enabled", true));

        settings.getEntityData().setEnabled(getBoolean(yamlData, "spoof.entity-data.enabled", true));
        settings.getEntityData().setPlayersOnly(getBoolean(yamlData, "spoof.entity-data.players-only.enabled", false));
        settings.getEntityData().setAirTicks(getBoolean(yamlData, "spoof.entity-data.air-ticks.enabled", true));
        settings.getEntityData().setHealth(getBoolean(yamlData, "spoof.entity-data.health.enabled", true));
        settings.getEntityData().setIgnoreVehicles(getBoolean(yamlData, "spoof.entity-data.health.ignore-vehicles", true));
        settings.getEntityData().getWolves().setEnabled(getBoolean(yamlData, "spoof.entity-data.health.ignore-wolves.enabled", true));
        settings.getEntityData().getWolves().setTamed(getBoolean(yamlData, "spoof.entity-data.health.ignore-wolves.when.for-tamed-wolves", false));
        settings.getEntityData().getWolves().setOwner(getBoolean(yamlData, "spoof.entity-data.health.ignore-wolves.when.for-owned-wolves", true));
        settings.getEntityData().getIronGolems().setEnabled(getBoolean(yamlData, "spoof.entity-data.health.ignore-iron-golems.enabled", true));
        settings.getEntityData().getIronGolems().setGradual(getBoolean(yamlData, "spoof.entity-data.health.ignore-iron-golems.gradual.enabled", true));
        settings.getEntityData().setAbsorption(getBoolean(yamlData, "spoof.entity-data.absorption.enabled", true));
        settings.getEntityData().setXp(getBoolean(yamlData, "spoof.entity-data.xp.enabled", true));
        settings.getItems().setEnabled(getBoolean(yamlData, "spoof.entity-data.items.enabled", true));
        settings.getItems().setStackAmount(getBoolean(yamlData, "spoof.entity-data.items.stack-amount.enabled", true));
        settings.getItems().setDurability(getBoolean(yamlData, "spoof.entity-data.items.durability.enabled", true));
        settings.getItems().setEnchantments(getBoolean(yamlData, "spoof.entity-data.items.enchantments.enabled", true));
    }

    private boolean getBoolean(Map<String, Object> yamlData, String key, boolean defaultValue) {
        String[] keys = key.split("\\.");
        Object value = findNestedValue(yamlData, keys, defaultValue);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    private Object findNestedValue(Map<String, Object> yamlData, String[] keys, Object defaultValue) {
        Object value = yamlData;
        for (String key : keys) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(key);
            } else {
                platform.getLogManager().severe("Invalid config structure for key: " + String.join(".", keys));
                return defaultValue;
            }
        }
        return value != null ? value : defaultValue;
    }
}

