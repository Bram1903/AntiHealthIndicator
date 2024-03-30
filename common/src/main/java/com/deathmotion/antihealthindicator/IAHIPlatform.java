package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.enums.ConfigOption;

/**
 * Represents a platform for the AntiHealthIndicator plugin.
 * @param <P> The platform type, for example, Bukkit its type is JavaPlugin.
 * @param <S> The sender type, for example, Bukkit its type is CommandSender.
 */
public interface IAHIPlatform<P, S> {

    boolean hasPermission(S sender, String permission);

    boolean getConfigurationOption(ConfigOption option);

    P getPlatform();

    void onLoad();

    void onEnable();

    void onDisable();

}
