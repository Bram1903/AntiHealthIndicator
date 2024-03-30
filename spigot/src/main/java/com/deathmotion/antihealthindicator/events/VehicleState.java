package com.deathmotion.antihealthindicator.events;

import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage player events related to vehicle state
 */
public class VehicleState implements Listener {

    private final ConfigManager configManager;
    private final CacheManager cacheManager;

    /**
     * Constructor for VehicleState
     *
     * @param plugin AntiHealthIndicator plugin
     */
    public VehicleState(AntiHealthIndicator plugin) {
        this.configManager = plugin.getConfigManager();
        this.cacheManager = plugin.getCacheManager();
    }

    /**
     * EventHandler method triggered when a player enters a vehicle
     *
     * @param event VehicleEnterEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRide(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player && event.getVehicle() instanceof LivingEntity) {
            Player player = (Player) event.getEntered();
            LivingEntity vehicle = (LivingEntity) event.getVehicle();
            handleVehicleEvent(player, vehicle, (float) vehicle.getHealth(), true);
        }
    }

    /**
     * EventHandler method triggered when a player exits a vehicle
     *
     * @param event VehicleExitEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onExitRide(VehicleExitEvent event) {
        if (event.getExited() instanceof Player && event.getVehicle() instanceof LivingEntity) {
            Player player = (Player) event.getExited();
            LivingEntity vehicle = (LivingEntity) event.getVehicle();
            handleVehicleEvent(player, vehicle, 0.5f, false);
        }
    }

    /**
     * Handle player entering/exiting a vehicle event.
     *
     * @param player      The player involved in the event.
     * @param vehicle     The vehicle involved in the event.
     * @param healthValue The health value for the vehicle.
     * @param isEntering  A boolean denoting whether the player is entering or exiting the vehicle.
     */
    private void handleVehicleEvent(Player player, LivingEntity vehicle, float healthValue, boolean isEntering) {
        if (configManager.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED)) {
            if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
        }

        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        List<EntityData> metadata = new ArrayList<>();
        metadata.add(new EntityData(MetadataIndex.HEALTH, EntityDataTypes.FLOAT, healthValue));
        user.writePacket(new WrapperPlayServerEntityMetadata(vehicle.getEntityId(), metadata));

        if (isEntering) {
            cacheManager.addVehicleToCache(player.getUniqueId(), vehicle.getEntityId());
        } else {
            cacheManager.removeVehicle(player.getUniqueId());
        }
    }
}