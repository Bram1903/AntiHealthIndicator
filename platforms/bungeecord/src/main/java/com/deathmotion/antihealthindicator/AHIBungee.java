package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.managers.BungeeConfigManager;
import com.deathmotion.antihealthindicator.schedulers.BungeeScheduler;
import net.md_5.bungee.api.plugin.Plugin;

public final class AHIBungee extends Plugin {
    private final BungeeAntiHealthIndicator ahi = new BungeeAntiHealthIndicator(this);

    @Override
    public void onEnable() {
        ahi.setScheduler(new BungeeScheduler(this));
        ahi.setBukkitConfigManager(new BungeeConfigManager(this));

        ahi.commonOnEnable();
        ahi.enableBStats();
    }

    @Override
    public void onDisable() {
        ahi.commonOnDisable();
    }
}
