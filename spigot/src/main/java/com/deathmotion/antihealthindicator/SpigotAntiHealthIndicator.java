package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import io.github.retrooper.packetevents.bstats.Metrics;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@Getter
public class SpigotAntiHealthIndicator extends AHIPlatform<JavaPlugin> {

    private final JavaPlugin plugin;
    private ConfigManager configManager;


    public SpigotAntiHealthIndicator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public JavaPlugin getPlatform() {
        return this.plugin;
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        CommandSender commandSender = Bukkit.getPlayer(sender);
        if (commandSender == null) return false;

        return commandSender.hasPermission(permission);
    }

    @Override
    public boolean isEntityRemoved(int entityId, Object playerObject) {
        Player player = (Player) playerObject;
        return SpigotConversionUtil.getEntityById(player.getWorld(), entityId) == null;
    }

    @Override
    public boolean getConfigurationOption(ConfigOption option) {
        return this.configManager.getConfigurationOption(option);
    }

    @Override
    public String getPluginVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public void enableBStats() {
        try {
            new Metrics(this.plugin, 20803);
        } catch (Exception e) {
            this.plugin.getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}