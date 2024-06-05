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
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager<P> {
    private final AHIPlatform<P> platform;
    private ConcurrentHashMap<ConfigOption, Boolean> configValues;

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
            configValues = new ConcurrentHashMap<>();

            // Get a file handle to the yaml
            File configFile = new File(platform.getPluginDirectory(), "config.yml");

            // Parse the yaml
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(new FileInputStream(configFile));

            // Transform data into Enum map
            for (ConfigOption option : ConfigOption.values()) {
                String[] keys = option.getKey().split("\\.");
                Object value = findNestedValue(yamlData, keys);
                configValues.put(option, value != null ? (Boolean) value : option.getDefaultValue());
            }
        } catch (FileNotFoundException e) {
            // Handle the exception...
            System.out.println("Config file not found!");
        }
    }

    private Object findNestedValue(Map<String, Object> yamlData, String[] keys) {
        if (keys.length == 0 || yamlData == null) {
            return null;
        }

        Object value = yamlData.get(keys[0]);
        if (keys.length > 1) {
            value = findNestedValue((Map<String, Object>) value, Arrays.copyOfRange(keys, 1, keys.length));
        }

        if (value == null) {
            platform.getLogManager().severe("Config value for " + String.join(".", keys) + " not found! Using default value.");
        }

        return value;
    }
}
