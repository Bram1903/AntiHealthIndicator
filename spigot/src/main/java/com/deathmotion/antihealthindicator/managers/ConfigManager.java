package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.enums.ConfigOption;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.Map;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final Map<ConfigOption, Object> configurationOptions = new EnumMap<>(ConfigOption.class);

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;

        saveDefaultConfiguration();
        loadConfigurationOptions();
    }

    private void saveDefaultConfiguration() {
        plugin.saveDefaultConfig();
    }

    private void loadConfigurationOptions() {
        for (ConfigOption option : ConfigOption.values()) {
            configurationOptions.put(option, plugin.getConfig().getBoolean(option.getKey(), option.getDefaultValue()));
        }
    }

    public Boolean getConfigurationOption(ConfigOption option) {
        return (Boolean) configurationOptions.get(option);
    }
}