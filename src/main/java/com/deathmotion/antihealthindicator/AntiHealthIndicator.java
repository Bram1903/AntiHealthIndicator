package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.events.UpdateNotifier;
import com.deathmotion.antihealthindicator.packetlisteners.PacketListenerManager;
import com.deathmotion.antihealthindicator.schedulers.ServerScheduler;
import com.deathmotion.antihealthindicator.schedulers.impl.BukkitScheduler;
import com.deathmotion.antihealthindicator.schedulers.impl.FoliaScheduler;
import com.deathmotion.antihealthindicator.util.UpdateChecker;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AntiHealthIndicator extends JavaPlugin {

    @Getter
    private static AntiHealthIndicator instance;
    private ServerScheduler scheduler;

    private final ConcurrentHashMap<Player, Integer> vehicles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Entity> entityDataMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private boolean isUpdateAvailable = false;

    @Getter
    @Setter
    private String latestVersion;

    private static ServerScheduler getCorrectScheduler(JavaPlugin plugin) {
        if (isFolia()) {
            return new FoliaScheduler(plugin);
        }

        return new BukkitScheduler(plugin);
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

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
        instance = this;

        saveDefaultConfig();

        scheduler = getCorrectScheduler(this);

        new PacketListenerManager(this).setupPacketListeners();

        updateChecker();
        enableBStats();

        getLogger().info("Plugin has successfully been initialized!");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        getLogger().info("Plugin has been uninitialized!");
    }

    private void updateChecker() {
        if (getConfig().getBoolean("update-checker.enabled", true)) {
            new UpdateChecker(this).checkForUpdate();

            if (getConfig().getBoolean("update-checker.notify-in-game", true)) {
                getServer().getPluginManager().registerEvents(new UpdateNotifier(), this);
            }
        }
    }

    private void enableBStats() {
        try {
            new Metrics(this, 20803);
        } catch (Exception e) {
            getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}
