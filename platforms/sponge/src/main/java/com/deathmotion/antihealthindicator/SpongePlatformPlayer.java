package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.models.PlatformPlayer;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class SpongePlatformPlayer extends PlatformPlayer {
    private final ServerPlayer player;

    public SpongePlatformPlayer(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}