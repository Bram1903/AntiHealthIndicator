package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.packetlisteners.EntityState;
import com.deathmotion.antihealthindicator.packetlisteners.PlayerJoin;
import com.deathmotion.antihealthindicator.packetlisteners.spoofers.*;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

public class PacketManager<P> {
    private final AHIPlatform<P> platform;

    public PacketManager(AHIPlatform<P> platform) {
        this.platform = platform;

        platform.getScheduler().runTaskLater(() -> {
            System.out.println("test");
        }, 20L);

        setupPacketListeners();
    }

    public void setupPacketListeners() {
        setupEntityListeners();
        setupAdditionalListeners();

        PacketEvents.getAPI().init();
    }

    private void setupEntityListeners() {
        if (platform.getConfigurationOption(ConfigOption.ENTITY_DATA_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new EntityState<>(this.platform));
            PacketEvents.getAPI().getEventManager().registerListener(new EntityMetadataListener<>(this.platform));

            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_15)) {
                PacketEvents.getAPI().getEventManager().registerListener(new SpawnLivingEntityListener<>(this.platform));
            }

            boolean spoofHealth = platform.getConfigurationOption(ConfigOption.HEALTH_ENABLED);
            boolean ignoreVehicles = platform.getConfigurationOption(ConfigOption.IGNORE_VEHICLES_ENABLED);

            if (spoofHealth && ignoreVehicles) {
                // TODO: Add a check listener that keeps track of the entities that are riding vehicles
            }
        }
    }

    private void setupAdditionalListeners() {
        if (platform.getConfigurationOption(ConfigOption.ITEMS_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new EntityEquipmentListener<>(this.platform));
        }

        if (platform.getConfigurationOption(ConfigOption.SPOOF_FOOD_SATURATION_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new PlayerUpdateHealthListener<>(this.platform));
        }
        if (platform.getConfigurationOption(ConfigOption.SPOOF_WORLD_SEED_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new WorldSeedListener<>(this.platform));
        }
        if (platform.getConfigurationOption(ConfigOption.ENCHANTMENTS_ENABLED)) {
            PacketEvents.getAPI().getEventManager().registerListener(new WindowItemsListener<>(this.platform));
        }
    }
}