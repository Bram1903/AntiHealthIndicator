package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.models.PlatformPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlatformPlayer extends PlatformPlayer {
    private final ProxiedPlayer player;

    public BungeePlatformPlayer(ProxiedPlayer player) {
        this.player = player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}