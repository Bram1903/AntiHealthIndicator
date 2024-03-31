package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@Getter
public class AntiHealthIndicator extends AHIPlatform<JavaPlugin> {

    private final JavaPlugin plugin;

    public AntiHealthIndicator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Getter
    private ConfigManager configManager;

    @Override
    public JavaPlugin getPlatform() {
        return this.plugin;
    }

    @Override
    public void onLoad() {
        super.commonOnLoad();

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(true);

        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        super.commonOnEnable();

        configManager = new ConfigManager(this.plugin);
        enableBStats();
    }

    @Override
    public void onDisable() {
        super.commonOnDisable();
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        CommandSender commandSender = Bukkit.getPlayer(sender);
        if (commandSender == null) return false;

        return commandSender.hasPermission(permission);
    }

    @Override
    public boolean getConfigurationOption(ConfigOption option) {
        return this.configManager.getConfigurationOption(option);
    }

    private void enableBStats() {
        try {
            new Metrics(this.plugin, 20803);
        } catch (Exception e) {
            this.plugin.getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}
