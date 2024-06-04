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
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.Map;

public class BungeeConfigManager {
    private final Plugin plugin;
    private final Map<ConfigOption, Object> configurationOptions = new EnumMap<>(ConfigOption.class);

    public BungeeConfigManager(Plugin plugin) {
        this.plugin = plugin;

        saveDefaultConfiguration();
        loadConfigurationOptions();
    }

    private void saveDefaultConfiguration() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = plugin.getResourceAsStream("config.yml");
                     OutputStream os = Files.newOutputStream(configFile.toPath())) {
                    ByteStreams.copy(is, os);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
    }

    private void loadConfigurationOptions() {
        Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(plugin.getResourceAsStream("config.yml"));

        for (ConfigOption option : ConfigOption.values()) {
            configurationOptions.put(option, configuration.get(option.getKey(), option.getDefaultValue()));
        }
    }

    public Boolean getConfigurationOption(ConfigOption option) {
        return (Boolean) configurationOptions.get(option);
    }
}
