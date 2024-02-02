package com.deathmotion.antihealthindicator.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotifier implements Listener {

    private final String latestVersion;

    public UpdateNotifier(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("AntiHealthIndicator.Notify")) {
            player.sendMessage("§c[AntiHealthIndicator] §a Version " + latestVersion + " is now available!");
        }
    }
}