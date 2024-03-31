package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;

public class WorldSeedListener<P> extends PacketListenerAbstract {

    private final AHIPlatform<P> platform;

    protected final boolean bypassPermissionEnabled;

    public WorldSeedListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.bypassPermissionEnabled = platform.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.JOIN_GAME)) {
            if (bypassPermissionEnabled) {
                if (platform.hasPermission(event.getUser().getUUID(), "AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerJoinGame wrapper = new WrapperPlayServerJoinGame(event);
            wrapper.setHashedSeed(0L);
            event.markForReEncode(true);
        }
        if (event.getPacketType().equals(PacketType.Play.Server.RESPAWN)) {
            if (bypassPermissionEnabled) {
                if (platform.hasPermission(event.getUser().getUUID(), "AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerRespawn wrapper = new WrapperPlayServerRespawn(event);
            wrapper.setHashedSeed(0L);
            event.markForReEncode(true);
        }
    }
}