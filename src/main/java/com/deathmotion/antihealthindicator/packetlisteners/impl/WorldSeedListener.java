package com.deathmotion.antihealthindicator.packetlisteners.impl;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldSeedListener extends PacketListenerAbstract {

    protected final boolean bypassPermissionEnabled;

    public WorldSeedListener(JavaPlugin plugin) {
        this.bypassPermissionEnabled = plugin.getConfig().getBoolean("allow-bypass.enabled", false);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.JOIN_GAME)) {
            if (bypassPermissionEnabled) {
                Player player = (Player) event.getPlayer();
                if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerJoinGame wrapper = new WrapperPlayServerJoinGame(event);
            wrapper.setHashedSeed(0L);
            event.markForReEncode(true);
        }
        if (event.getPacketType().equals(PacketType.Play.Server.RESPAWN)) {
            if (bypassPermissionEnabled) {
                Player player = (Player) event.getPlayer();
                if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerRespawn wrapper = new WrapperPlayServerRespawn(event);
            wrapper.setHashedSeed(0L);
            event.markForReEncode(true);
        }
    }
}