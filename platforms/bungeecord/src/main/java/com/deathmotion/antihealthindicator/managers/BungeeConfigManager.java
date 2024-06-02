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
