package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import com.deathmotion.antihealthindicator.managers.BungeeConfigManager;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.bstats.Metrics;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@Getter
public class BungeeAntiHealthIndicator extends AHIPlatform<Plugin> {

    private final Plugin plugin;
    private BungeeConfigManager bungeeConfigManager;

    public BungeeAntiHealthIndicator(Plugin plugin) {
        this.plugin = plugin;
    }

    protected void setBukkitConfigManager(BungeeConfigManager bungeeConfigManager) {
        this.bungeeConfigManager = bungeeConfigManager;
    }

    @Override
    public Plugin getPlatform() {
        return this.plugin;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    protected void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(sender);
        if (player == null) return false;

        return player.hasPermission(permission);
    }

    @Override
    public boolean getConfigurationOption(ConfigOption option) {
        return this.bungeeConfigManager.getConfigurationOption(option);
    }

    protected void enableBStats() {
        try {
            new Metrics(this.plugin, 20803);
        } catch (Exception e) {
            this.plugin.getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}
