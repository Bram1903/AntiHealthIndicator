package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.models.PlatformPlayer;
import com.velocitypowered.api.proxy.Player;

public class VelocityPlatformPlayer extends PlatformPlayer {

    private final Player player;

    public VelocityPlatformPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}
