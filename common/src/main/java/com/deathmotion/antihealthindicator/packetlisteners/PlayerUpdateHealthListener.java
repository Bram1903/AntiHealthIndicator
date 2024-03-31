package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;

public class PlayerUpdateHealthListener<P> extends PacketListenerAbstract {


    private final AHIPlatform<P> platform;
    private final boolean bypassPermissionEnabled;

    public PlayerUpdateHealthListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.bypassPermissionEnabled = platform.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.UPDATE_HEALTH) {
            if (bypassPermissionEnabled) {
                if (platform.hasPermission(event.getUser().getUUID(), "AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerUpdateHealth packet = new WrapperPlayServerUpdateHealth(event);

            if (packet.getFoodSaturation() > 0) {
                packet.setFoodSaturation(Float.NaN);
                event.markForReEncode(true);
            }
        }
    }
}
