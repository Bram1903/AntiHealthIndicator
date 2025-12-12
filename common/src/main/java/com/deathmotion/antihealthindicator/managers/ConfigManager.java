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

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.models.Settings;
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
        File pluginDirectory = new File(platform.getPluginDirectory());
        File configFile = new File(pluginDirectory, "config.yml");

        if (!pluginDirectory.exists() && !pluginDirectory.mkdirs()) {
            platform.getLogManager().severe("Failed to create plugin directory: " + pluginDirectory.getAbsolutePath());
            return;
        }

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
            return;
        }

        try (InputStream inputStream = Files.newInputStream(configFile.toPath())) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(inputStream);

            this.settings = new Settings();
            setConfigOptions(yamlData, this.settings);
        } catch (IOException e) {
            platform.getLogManager().severe("Failed to load configuration: " + e.getMessage());
            platform.commonOnDisable();
        }
    }

    public void reloadConfig() {
        File configFile = new File(platform.getPluginDirectory(), "config.yml");

        if (!configFile.exists()) {
            platform.getLogManager().severe("Recreating config file as it does not exist");
            saveDefaultConfiguration();
        }

        loadConfig();
    }

    private void setConfigOptions(Map<String, Object> yaml, Settings settings) {
        settings.setDebug(getValue(yaml, "debug.enabled", Boolean.class, false));
        settings.getUpdateChecker().setEnabled(getValue(yaml, "update-checker.enabled", Boolean.class, true));
        settings.getUpdateChecker().setPrintToConsole(getValue(yaml, "update-checker.print-to-console", Boolean.class, true));
        settings.getUpdateChecker().setNotifyInGame(getValue(yaml, "update-checker.notify-in-game", Boolean.class, true));

        settings.setFoodSaturation(getValue(yaml, "spoof.food-saturation.enabled", Boolean.class, false));
        settings.setTeamScoreboard(getValue(yaml, "spoof.team-scoreboard.enabled", Boolean.class, true));
        settings.setGamemode(getValue(yaml, "spoof.gamemode.enabled", Boolean.class, true));

        setEntityDataOptions(yaml, settings);
        setItemOptions(yaml, settings);
    }

    private void setEntityDataOptions(Map<String, Object> yaml, Settings settings) {
        settings.getEntityData().setEnabled(getValue(yaml, "spoof.entity-data.enabled", Boolean.class, true));
        settings.getEntityData().setPlayersOnly(getValue(yaml, "spoof.entity-data.players-only.enabled", Boolean.class, false));
        settings.getEntityData().setAirTicks(getValue(yaml, "spoof.entity-data.air-ticks.enabled", Boolean.class, true));
        settings.getEntityData().setHealth(getValue(yaml, "spoof.entity-data.health.enabled", Boolean.class, true));

        float rawHealthValue = getValue(yaml, "spoof.entity-data.health.spoof-value", Float.class, 1f);
        float clampedHealthValue = clampFloat(rawHealthValue, 1f, Integer.MAX_VALUE, "spoof.entity-data.health.spoof-value");
        settings.getEntityData().setHealthValue(clampedHealthValue);

        settings.getEntityData().setRandomizeHealth(getValue(yaml, "spoof.entity-data.health.randomize", Boolean.class, false));
        settings.getEntityData().setAbsorption(getValue(yaml, "spoof.entity-data.absorption.enabled", Boolean.class, true));
        settings.getEntityData().setXp(getValue(yaml, "spoof.entity-data.xp.enabled", Boolean.class, true));
    }

    private void setItemOptions(Map<String, Object> yaml, Settings settings) {
        settings.getItems().setEnabled(getValue(yaml, "spoof.entity-data.items.enabled", Boolean.class, true));
        settings.getItems().setStackAmount(getValue(yaml, "spoof.entity-data.items.stack-amount.enabled", Boolean.class, true));
        settings.getItems().setDurability(getValue(yaml, "spoof.entity-data.items.durability.enabled", Boolean.class, true));
        settings.getItems().setEnchantments(getValue(yaml, "spoof.entity-data.items.enchantments.enabled", Boolean.class, true));
    }

    private <T> T getValue(Map<String, Object> yamlData, String key, Class<T> type, T defaultValue) {
        Object raw = findNestedValue(yamlData, key.split("\\."));
        if (raw == null) {
            platform.getLogManager().warn("Config value '" + key + "' not found, using default: " + defaultValue);
            return defaultValue;
        }

        if (type.isInstance(raw)) {
            return type.cast(raw);
        }

        if (raw instanceof Number) {
            Number n = (Number) raw;
            try {
                if (type == Integer.class) {
                    return type.cast(n.intValue());
                }
                if (type == Long.class) {
                    return type.cast(n.longValue());
                }
                if (type == Float.class) {
                    return type.cast(n.floatValue());
                }
                if (type == Double.class) {
                    return type.cast(n.doubleValue());
                }
            } catch (Exception e) {
                platform.getLogManager().warn("Failed numeric conversion for key '" + key + "' (" + raw + "): " + e.getMessage() + ". Using default: " + defaultValue);
                return defaultValue;
            }
        }

        if (raw instanceof String) {
            String s = (String) raw;
            try {
                if (type == Integer.class) {
                    return type.cast(Integer.parseInt(s));
                }
                if (type == Long.class) {
                    return type.cast(Long.parseLong(s));
                }
                if (type == Float.class) {
                    return type.cast(Float.parseFloat(s));
                }
                if (type == Double.class) {
                    return type.cast(Double.parseDouble(s));
                }
                if (type == Boolean.class) {
                    return type.cast(Boolean.parseBoolean(s));
                }
            } catch (Exception e) {
                platform.getLogManager().warn("Failed to parse config value for key '" + key + "' from string '" + s + "': " + e.getMessage() + ". Using default: " + defaultValue);
                return defaultValue;
            }
        }

        if (raw instanceof Boolean && type != Boolean.class) {
            platform.getLogManager().warn("Config value '" + key + "' is Boolean but expected " + type.getSimpleName() + ". Using default: " + defaultValue);
            return defaultValue;
        }

        platform.getLogManager().warn("Config value '" + key + "' has incompatible type " +
                "'" + raw.getClass().getSimpleName() + "', expected '" + type.getSimpleName() +
                "'. Using default: " + defaultValue);
        return defaultValue;
    }

    private float clampFloat(float value, float min, float max, String key) {
        if (value < min) {
            platform.getLogManager().warn("Config value '" + key + "' (" + value +
                    ") is below minimum " + min + ", clamping to " + min + ".");
            return min;
        }
        if (value > max) {
            platform.getLogManager().warn("Config value '" + key + "' (" + value +
                    ") is above maximum " + max + ", clamping to " + max + ".");
            return max;
        }
        return value;
    }

    private Object findNestedValue(Map<String, Object> yamlData, String[] keys) {
        Object value = yamlData;
        StringBuilder path = new StringBuilder();

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (i > 0) {
                path.append('.');
            }
            path.append(key);

            if (!(value instanceof Map)) {
                platform.getLogManager().warn("Invalid config structure at '" + path + "': expected a section.");
                return null;
            }

            value = ((Map<?, ?>) value).get(key);
            if (value == null) {
                return null;
            }
        }

        return value;
    }
}
