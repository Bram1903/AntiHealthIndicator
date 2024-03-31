package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.enums.ConfigOption;

import java.util.UUID;

public abstract class AHIPlatform<P> extends CommonAHIPlatform {
    public abstract P getPlatform();

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract boolean hasPermission(UUID sender, String permission);

    public abstract boolean getConfigurationOption(ConfigOption option);
}