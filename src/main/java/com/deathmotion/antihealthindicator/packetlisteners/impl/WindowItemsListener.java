package com.deathmotion.antihealthindicator.packetlisteners.impl;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

public class WindowItemsListener extends PacketListenerAbstract {
    protected final boolean bypassPermissionEnabled;

    public WindowItemsListener(JavaPlugin plugin) {
        this.bypassPermissionEnabled = plugin.getConfig().getBoolean("allow-bypass.enabled", false);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            Player player = (Player) event.getPlayer();

            if (bypassPermissionEnabled) {
                if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);

            if (player.getOpenInventory().getType().equals(InventoryType.ENCHANTING) && wrapper.getStateId() == 3)
                event.setCancelled(true);
        }
    }
}