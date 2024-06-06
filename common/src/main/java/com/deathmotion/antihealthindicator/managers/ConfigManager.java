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
            for (ConfigOption option : ConfigOption.values()) {
                setConfigOption(yamlData, settings, option);
            }

            this.settings = settings;
        } catch (IOException e) {
            platform.getLogManager().severe("Failed to load configuration: " + e.getMessage());
            platform.commonOnDisable();
        }
    }

    private void setConfigOption(Map<String, Object> yamlData, Settings settings, ConfigOption option) {
        String[] keys = option.getKey().split("\\.");
        Object value = findNestedValue(yamlData, keys, option.getDefaultValue());

        switch (option) {
            case DEBUG_ENABLED:
                settings.setDebug((Boolean) value);
                break;
            case UPDATE_CHECKER_ENABLED:
                settings.getUpdateChecker().setEnabled((Boolean) value);
                break;
            case UPDATE_CHECKER_PRINT_TO_CONSOLE:
                settings.getUpdateChecker().setPrintToConsole((Boolean) value);
                break;
            case NOTIFY_IN_GAME:
                settings.getUpdateChecker().setNotifyInGame((Boolean) value);
                break;
            case ALLOW_BYPASS_ENABLED:
                settings.setAllowBypass((Boolean) value);
                break;
            case SPOOF_WORLD_SEED_ENABLED:
                settings.setWorldSeed((Boolean) value);
                break;
            case SPOOF_FOOD_SATURATION_ENABLED:
                settings.setFoodSaturation((Boolean) value);
                break;
            case ENTITY_DATA_ENABLED:
                settings.getEntityData().setEnabled((Boolean) value);
                break;
            case PLAYER_ONLY:
                settings.getEntityData().setPlayersOnly((Boolean) value);
                break;
            case AIR_TICKS_ENABLED:
                settings.getEntityData().setAirTicks((Boolean) value);
                break;
            case HEALTH_ENABLED:
                settings.getEntityData().setHealth((Boolean) value);
                break;
            case IGNORE_VEHICLES_ENABLED:
                settings.getEntityData().setIgnoreVehicles((Boolean) value);
                break;
            case IGNORE_WOLVES_ENABLED:
                settings.getEntityData().getWolves().setEnabled((Boolean) value);
                break;
            case FOR_TAMED_WOLVES_ENABLED:
                settings.getEntityData().getWolves().setTamed((Boolean) value);
                break;
            case FOR_OWNED_WOLVES_ENABLED:
                settings.getEntityData().getWolves().setOwner((Boolean) value);
                break;
            case IGNORE_IRON_GOLEMS_ENABLED:
                settings.getEntityData().getIronGolems().setEnabled((Boolean) value);
                break;
            case GRADUAL_IRON_GOLEM_HEALTH_ENABLED:
                settings.getEntityData().getIronGolems().setGradual((Boolean) value);
                break;
            case ABSORPTION_ENABLED:
                settings.getEntityData().setAbsorption((Boolean) value);
                break;
            case XP_ENABLED:
                settings.getEntityData().setXp((Boolean) value);
                break;
            case ITEMS_ENABLED:
                settings.getItems().setEnabled((Boolean) value);
                break;
            case STACK_AMOUNT_ENABLED:
                settings.getItems().setStackAmount((Boolean) value);
                break;
            case DURABILITY_ENABLED:
                settings.getItems().setDurability((Boolean) value);
                break;
            case ENCHANTMENTS_ENABLED:
                settings.getItems().setEnchantments((Boolean) value);
                break;
            default:
                platform.getLogManager().severe("Unknown config option: " + option);
        }
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

