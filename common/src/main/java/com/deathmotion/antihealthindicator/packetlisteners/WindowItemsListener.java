package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;

public class WindowItemsListener<P> extends PacketListenerAbstract {

    protected final boolean bypassPermissionEnabled;
    private final AHIPlatform<P> platform;

    public WindowItemsListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.bypassPermissionEnabled = platform.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            User user = event.getUser();

            if (bypassPermissionEnabled) {
                if (platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass")) return;
            }

            WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);

            // TODO: Tofaa, would this be a spigot only check?
            if (user.getOpenInventory().getType().equals(InventoryType.ENCHANTING) && wrapper.getStateId() == 3)
                event.setCancelled(true);
        }
    }
}