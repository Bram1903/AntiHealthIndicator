package com.deathmotion.antihealthindicator.spoofers.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.injector.ChannelInjector;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
import com.github.retrooper.packetevents.manager.server.ServerManager;
import com.github.retrooper.packetevents.netty.NettyManager;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.PatchableComponentMap;
import com.github.retrooper.packetevents.protocol.component.StaticComponentMap;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MetadataSpooferTest {

    static {
        PacketEvents.setAPI(new TestPacketEventsAPI());
    }

    @Test
    void removesDroppedItemNameComponents() {
        PatchableComponentMap components = new PatchableComponentMap(StaticComponentMap.EMPTY);
        components.set(ComponentTypes.CUSTOM_NAME, Component.text("Diamond"));
        components.set(ComponentTypes.ITEM_NAME, Component.text("Rare Diamond"));

        MetadataSpoofer.removeNameComponents(components);

        assertFalse(components.has(ComponentTypes.CUSTOM_NAME));
        assertFalse(components.has(ComponentTypes.ITEM_NAME));
    }

    private static final class TestPacketEventsAPI extends PacketEventsAPI<Object> {
        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public void init() {
        }

        @Override
        public void load() {
        }

        @Override
        public boolean isInitialized() {
            return true;
        }

        @Override
        public void terminate() {
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public Object getPlugin() {
            return null;
        }

        @Override
        public ServerManager getServerManager() {
            return null;
        }

        @Override
        public ProtocolManager getProtocolManager() {
            return null;
        }

        @Override
        public PlayerManager getPlayerManager() {
            return null;
        }

        @Override
        public NettyManager getNettyManager() {
            return null;
        }

        @Override
        public ChannelInjector getInjector() {
            return null;
        }
    }
}
