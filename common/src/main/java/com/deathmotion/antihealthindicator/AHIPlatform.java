package com.deathmotion.antihealthindicator;

/**
 * Represents a platform for the AntiHealthIndicator plugin.
 * @param <P> The platform type, for example, Bukkit its type is JavaPlugin.
 * @param <S> The sender type, for example, Bukkit its type is CommandSender.
 */
public interface AHIPlatform<P, S> {

    boolean hasPermission(S sender, String permission);

    P getPlatform();

    void onLoad();

    void onEnable();

    void onDisable();

}
