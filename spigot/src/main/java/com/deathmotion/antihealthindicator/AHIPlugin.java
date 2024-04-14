package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.schedulers.SpigotScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class AHIPlugin extends JavaPlugin {
    private final SpigotAntiHealthIndicator ahi = new SpigotAntiHealthIndicator(this);

    public void onLoad() {
        ahi.commonOnLoad();
    }

    @Override
    public void onEnable() {
        ahi.setScheduler(new SpigotScheduler(this));
        ahi.setConfigManager(new ConfigManager(this));

        ahi.commonOnEnable();
        ahi.enableBStats();
    }

    @Override
    public void onDisable() {
        ahi.commonOnDisable();
    }
}