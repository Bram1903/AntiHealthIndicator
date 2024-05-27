package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VelocityAntiHealthIndicator extends AHIPlatform<ProxyServer>{

    private final ProxyServer proxy;

    public VelocityAntiHealthIndicator(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public ProxyServer getPlatform() {
        return this.proxy;
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        Player player = this.proxy.getPlayer(sender).orElse(null);
        if (player == null) return false;

        return player.hasPermission(permission);
    }

    @Override
    public void broadcastComponent(Component component, @Nullable String permission) {
        this.proxy.getAllPlayers().stream()
                .filter(player -> permission == null || player.hasPermission(permission))
                .forEach(player -> player.sendMessage(component));
    }

    @Override
    public boolean isEntityRemoved(int entityId, @Nullable Object player) {
        return false;
    }

    @Override
    public boolean getConfigurationOption(ConfigOption option) {
        return false;
    }

    @Override
    public String getPluginVersion() {
        return this.proxy.getVersion().toString();
    }
}
