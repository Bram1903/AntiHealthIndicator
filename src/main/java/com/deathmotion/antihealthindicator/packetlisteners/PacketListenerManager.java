package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.cache.CacheManager;
import com.deathmotion.antihealthindicator.events.EntityState;
import com.deathmotion.antihealthindicator.events.VehicleState;
import com.deathmotion.antihealthindicator.packetlisteners.impl.*;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class PacketListenerManager {
    private final JavaPlugin plugin;
    private final Configuration config;

    public PacketListenerManager(AntiHealthIndicator plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void setupPacketListeners() {
        setupEntityListeners();
        setupAdditionalListeners();

        PacketEvents.getAPI().init();
    }

    private void setupEntityListeners() {
        if (this.plugin.getConfig().getBoolean("spoof.entity-data.enabled", true)) {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
                setupEntityCache();
            }

            PacketEvents.getAPI().getEventManager().registerListener(new EntityMetadataListener(this.plugin));

            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_15)) {
                PacketEvents.getAPI().getEventManager().registerListener(new SpawnLivingEntityListener(this.plugin));
            }

            boolean spoofHealth = this.config.getBoolean("spoof.entity-data.health.enabled", true);
            boolean ignoreVehicles = this.config.getBoolean("spoof.entity-data.health.ignore-vehicles.enabled", true);
            if (spoofHealth && ignoreVehicles) {
                this.plugin.getServer().getPluginManager().registerEvents(new VehicleState(this.plugin), this.plugin);
            }

            if (this.config.getBoolean("spoof.entity-data.items.enabled", true)) {
                PacketEvents.getAPI().getEventManager().registerListener(new EntityEquipmentListener(this.plugin));
            }
        }
    }

    private void setupAdditionalListeners() {
        if (this.config.getBoolean("spoof.food-saturation.enabled", true)) {
            PacketEvents.getAPI().getEventManager().registerListener(new PlayerUpdateHealthListener(this.plugin));
        }
        if (this.config.getBoolean("spoof.world-seed.enabled", false)) {
            PacketEvents.getAPI().getEventManager().registerListener(new WorldSeedListener(this.plugin));
        }
        if (this.config.getBoolean("spoof.enchant-seed.enabled", false)) {
            PacketEvents.getAPI().getEventManager().registerListener(new WindowItemsListener(this.plugin));
        }
    }

    private void setupEntityCache() {
        this.plugin.getServer().getPluginManager().registerEvents(new EntityState(), this.plugin);
        new CacheManager().cacheLivingEntityData();
    }
}