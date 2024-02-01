package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import org.bukkit.entity.Player;

public class WorldSeedListener extends PacketListenerAbstract {

    protected final boolean bypassPermissionEnabled;

    public WorldSeedListener(AntiHealthIndicator plugin) {
        this.bypassPermissionEnabled = plugin.getConfigManager().getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
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