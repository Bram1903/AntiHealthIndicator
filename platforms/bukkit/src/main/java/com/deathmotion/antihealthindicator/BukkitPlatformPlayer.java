package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.models.PlatformPlayer;
import org.bukkit.entity.Player;

public class BukkitPlatformPlayer extends PlatformPlayer {
    private final Player player;

    public BukkitPlatformPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}