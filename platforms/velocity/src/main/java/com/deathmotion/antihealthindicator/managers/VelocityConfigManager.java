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

import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.Map;

public class VelocityConfigManager {
    private final Logger logger;
    private final Path dataDirectory;

    private final Map<ConfigOption, Object> configurationOptions = new EnumMap<>(ConfigOption.class);

    @Inject
    public VelocityConfigManager(Logger logger, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        saveDefaultConfiguration();
        loadConfigurationOptions();
    }

    private void saveDefaultConfiguration() {
        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            Path configPath = dataDirectory.resolve("config.toml");

            if (!Files.exists(configPath)) {
                InputStream resourceFile = getClass().getResourceAsStream("/config.toml");
                if (resourceFile != null) {
                    Files.copy(resourceFile, configPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    logger.warn("The file config.toml not found in resources.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadConfigurationOptions() {
        Path configPath = dataDirectory.resolve("config.toml");

        try {
            Reader reader = Files.newBufferedReader(configPath);
            Toml toml = new Toml().read(reader);

            for (ConfigOption option : ConfigOption.values()) {
                Boolean value = toml.getBoolean(option.getKey());
                if (value == null) {
                    value = option.getDefaultValue();
                }
                configurationOptions.put(option, value);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration: ", e);
        }
    }

    public Boolean getConfigurationOption(ConfigOption option) {
        return (Boolean) configurationOptions.get(option);
    }
}