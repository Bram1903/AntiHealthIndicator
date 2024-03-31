package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;

public class PlayerJoin<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;

    public PlayerJoin(AHIPlatform<P> platform) {
        this.platform = platform;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketType.Play.Server.JOIN_GAME == event.getPacketType()) {
            User user = event.getUser();

            if (platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Notify")) {
                user.sendMessage("Test");
            }
        }
    }
}