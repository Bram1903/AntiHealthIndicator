package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.packetlisteners.*;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.events.EntityState;
import com.deathmotion.antihealthindicator.events.VehicleState;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

public class PacketManager {
    private final AntiHealthIndicator plugin;
    private final ConfigManager config;

    public PacketManager(AntiHealthIndicator plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();

        setupPacketListeners();
    }

    public void setupPacketListeners() {
        setupEntityListeners();
        setupAdditionalListeners();

        PacketEvents.getAPI().init();
    }

    private void setupEntityListeners() {
        if (config.getConfigurationOption(ConfigOption.ENTITY_DATA_ENABLED)) {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
                setupEntityCache();
            }

            PacketEvents.getAPI().getEventManager().registerListener(new EntityMetadataListener(this.plugin));

            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_15)) {
                PacketEvents.getAPI().getEventManager().registerListener(new SpawnLivingEntityListener(this.plugin));
            }

            boolean spoofHealth = config.getConfigurationOption(ConfigOption.HEALTH_ENABLED);
            boolean ignoreVehicles = config.getConfigurationOption(ConfigOption.IGNORE_VEHICLES_ENABLED);
            if (spoofHealth && ignoreVehicles) {
                this.plugin.getServer().getPluginManager().registerEvents(new VehicleState(this.plugin), this.plugin);
            }

            if (config.getConfigurationOption(ConfigOption.ITEMS_ENABLED)) {
                PacketEvents.getAPI().getEventManager().registerListener(new EntityEquipmentListener(this.plugin));
            }
        }
    }

    private void setupAdditionalListeners() {
        if (config.getConfigurationOption(ConfigOption.SPOOF_FOOD_SATURATION_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new PlayerUpdateHealthListener(this.plugin));
        }
        if (config.getConfigurationOption(ConfigOption.SPOOF_WORLD_SEED_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new WorldSeedListener(this.plugin));
        }
        if (config.getConfigurationOption(ConfigOption.ENCHANTMENTS_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new WindowItemsListener(this.plugin));
        }
    }

    private void setupEntityCache() {
        this.plugin.getServer().getPluginManager().registerEvents(new EntityState(this.plugin), this.plugin);
        this.plugin.getCacheManager().cacheLivingEntityData();
    }
}