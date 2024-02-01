package com.deathmotion.antihealthindicator.events;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.managers.UpdateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotifier implements Listener {

    private final UpdateManager updateManager;

    public UpdateNotifier(AntiHealthIndicator plugin) {
        this.updateManager = plugin.getUpdateManager();
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!updateManager.isUpdateAvailable()) return;

        Player player = event.getPlayer();

        if (player.hasPermission("AntiHealthIndicator.Notify")) {
            player.sendMessage("§c[AntiHealthIndicator] §a Version " + updateManager.getLatestVersion() + " is now available!");
        }
    }
}