package com.deathmotion.antihealthindicator.packetlisteners.impl;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerUpdateHealthListener extends PacketListenerAbstract {

    protected final boolean bypassPermissionEnabled;

    public PlayerUpdateHealthListener(JavaPlugin plugin) {
        this.bypassPermissionEnabled = plugin.getConfig().getBoolean("allow-bypass.enabled", false);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_HEALTH) {
            if (bypassPermissionEnabled) {
                Player player = (Player) event.getPlayer();
                if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerUpdateHealth packet = new WrapperPlayServerUpdateHealth(event);

            if (packet.getFoodSaturation() > 0) {
                packet.setFoodSaturation(Float.NaN);
            }
        }
    }
}
