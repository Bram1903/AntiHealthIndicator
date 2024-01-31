package com.deathmotion.antihealthindicator.events;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotifier implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!AntiHealthIndicator.getInstance().isUpdateAvailable()) return;

        Player player = event.getPlayer();

        if (player.hasPermission("AntiHealthIndicator.Notify")) {
            player.sendMessage("§c[AntiHealthIndicator] §a Version " + AntiHealthIndicator.getInstance().getLatestVersion() + " is now available!");
        }
    }
}