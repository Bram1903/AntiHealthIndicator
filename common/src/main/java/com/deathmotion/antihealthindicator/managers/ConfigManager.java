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
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

public class ConfigManager<P> {
    private final AHIPlatform<P> platform;
    private Settings settings;

    public ConfigManager(AHIPlatform<P> platform) {
        this.platform = platform;

        saveDefaultConfiguration();
        loadConfig();
    }

    private void saveDefaultConfiguration() {
        try {
            File configFile = new File(platform.getPluginDirectory(), "config.yml");
            if (!configFile.exists()) {
                Files.copy(getClass().getResourceAsStream("/config.yml"), configFile.toPath());
            }
        } catch (Exception e) {
            platform.getLogManager().severe("Failed to save default configuration file!");
        }
    }

    private void loadConfig() {
        try {
            File configFile = new File(platform.getPluginDirectory(), "config.yml");

            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(new FileInputStream(configFile));

            Settings settings = new Settings();
            settings.setDebug(findNestedValue(yamlData, ConfigOption.DEBUG_ENABLED.getKey().split("\\."), ConfigOption.DEBUG_ENABLED));
            settings.getUpdateChecker().setEnabled(findNestedValue(yamlData, ConfigOption.UPDATE_CHECKER_ENABLED.getKey().split("\\."), ConfigOption.UPDATE_CHECKER_ENABLED));
            settings.getUpdateChecker().setPrintToConsole(findNestedValue(yamlData, ConfigOption.UPDATE_CHECKER_PRINT_TO_CONSOLE.getKey().split("\\."), ConfigOption.UPDATE_CHECKER_PRINT_TO_CONSOLE));
            settings.getUpdateChecker().setNotifyInGame(findNestedValue(yamlData, ConfigOption.NOTIFY_IN_GAME.getKey().split("\\."), ConfigOption.NOTIFY_IN_GAME));
            settings.setAllowBypass(findNestedValue(yamlData, ConfigOption.ALLOW_BYPASS_ENABLED.getKey().split("\\."), ConfigOption.ALLOW_BYPASS_ENABLED));
            settings.setWorldSeed(findNestedValue(yamlData, ConfigOption.SPOOF_WORLD_SEED_ENABLED.getKey().split("\\."), ConfigOption.SPOOF_WORLD_SEED_ENABLED));
            settings.setFoodSaturation(findNestedValue(yamlData, ConfigOption.SPOOF_FOOD_SATURATION_ENABLED.getKey().split("\\."), ConfigOption.SPOOF_FOOD_SATURATION_ENABLED));
            settings.getEntityData().setEnabled(findNestedValue(yamlData, ConfigOption.ENTITY_DATA_ENABLED.getKey().split("\\."), ConfigOption.ENTITY_DATA_ENABLED));
            settings.getEntityData().setPlayersOnly(findNestedValue(yamlData, ConfigOption.PLAYER_ONLY.getKey().split("\\."), ConfigOption.PLAYER_ONLY));
            settings.getEntityData().setAirTicks(findNestedValue(yamlData, ConfigOption.AIR_TICKS_ENABLED.getKey().split("\\."), ConfigOption.AIR_TICKS_ENABLED));
            settings.getEntityData().setHealth(findNestedValue(yamlData, ConfigOption.HEALTH_ENABLED.getKey().split("\\."), ConfigOption.HEALTH_ENABLED));
            settings.getEntityData().setIgnoreVehicles(findNestedValue(yamlData, ConfigOption.IGNORE_VEHICLES_ENABLED.getKey().split("\\."), ConfigOption.IGNORE_VEHICLES_ENABLED));
            settings.getEntityData().getWolves().setEnabled(findNestedValue(yamlData, ConfigOption.IGNORE_WOLVES_ENABLED.getKey().split("\\."), ConfigOption.IGNORE_WOLVES_ENABLED));
            settings.getEntityData().getWolves().setTamed(findNestedValue(yamlData, ConfigOption.FOR_TAMED_WOLVES_ENABLED.getKey().split("\\."), ConfigOption.FOR_TAMED_WOLVES_ENABLED));
            settings.getEntityData().getWolves().setOwner(findNestedValue(yamlData, ConfigOption.FOR_OWNED_WOLVES_ENABLED.getKey().split("\\."), ConfigOption.FOR_OWNED_WOLVES_ENABLED));
            settings.getEntityData().getIronGolems().setEnabled(findNestedValue(yamlData, ConfigOption.IGNORE_IRON_GOLEMS_ENABLED.getKey().split("\\."), ConfigOption.IGNORE_IRON_GOLEMS_ENABLED));
            settings.getEntityData().getIronGolems().setGradual(findNestedValue(yamlData, ConfigOption.GRADUAL_IRON_GOLEM_HEALTH_ENABLED.getKey().split("\\."), ConfigOption.GRADUAL_IRON_GOLEM_HEALTH_ENABLED));
            settings.setAbsorption(findNestedValue(yamlData, ConfigOption.ABSORPTION_ENABLED.getKey().split("\\."), ConfigOption.ABSORPTION_ENABLED));
            settings.setXp(findNestedValue(yamlData, ConfigOption.XP_ENABLED.getKey().split("\\."), ConfigOption.XP_ENABLED));
            settings.getItems().setEnabled(findNestedValue(yamlData, ConfigOption.ITEMS_ENABLED.getKey().split("\\."), ConfigOption.ITEMS_ENABLED));
            settings.getItems().setStackAmount(findNestedValue(yamlData, ConfigOption.STACK_AMOUNT_ENABLED.getKey().split("\\."), ConfigOption.STACK_AMOUNT_ENABLED));
            settings.getItems().setDurability(findNestedValue(yamlData, ConfigOption.DURABILITY_ENABLED.getKey().split("\\."), ConfigOption.DURABILITY_ENABLED));
            settings.getItems().setEnchantments(findNestedValue(yamlData, ConfigOption.ENCHANTMENTS_ENABLED.getKey().split("\\."), ConfigOption.ENCHANTMENTS_ENABLED));

            this.settings = settings;
        } catch (FileNotFoundException e) {
            // Handle the exception...
            System.out.println("Config file not found!");
            this.platform.commonOnDisable();
        }
    }

    private boolean findNestedValue(Map<String, Object> yamlData, String[] keys, ConfigOption configOption) {
        if (keys.length == 0 || yamlData == null) {
            this.platform.getLogManager().severe("Config value for " + String.join(".", keys) + " not found! Using default value.");
            return configOption.getDefaultValue();
        }

        Object value = yamlData.get(keys[0]);
        if (keys.length > 1) {
            value = findNestedValue((Map<String, Object>) value, Arrays.copyOfRange(keys, 1, keys.length), configOption);
        }

        if (value == null) {
            platform.getLogManager().severe("Config value for " + String.join(".", keys) + " not found! Using default value.");
            value = configOption.getDefaultValue();
        }

        return (boolean) value;
    }
}
