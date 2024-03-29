package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.managers.PacketManager;
import com.deathmotion.antihealthindicator.managers.UpdateManager;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class AntiHealthIndicator extends JavaPlugin {

    @Getter
    private ConfigManager configManager;
    private CacheManager cacheManager;
    private BukkitAudiences adventure;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false)
                .bStats(true);

        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        cacheManager = new CacheManager(this);
        adventure = BukkitAudiences.create(this);

        new UpdateManager(this);
        new PacketManager(this);

        enableBStats();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        adventure.close();
        getLogger().info("Plugin has been uninitialized!");
    }

    private void enableBStats() {
        try {
            new Metrics(this, 20803);
        } catch (Exception e) {
            getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}
