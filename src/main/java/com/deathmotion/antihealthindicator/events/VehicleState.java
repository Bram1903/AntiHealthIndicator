package com.deathmotion.antihealthindicator.events;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.util.entity.EntityMetadataIndex;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class VehicleState implements Listener {

    protected final boolean bypassPermissionEnabled;

    private final ConcurrentHashMap<Player, Integer> vehicles = AntiHealthIndicator.getInstance().getVehicles();

    public VehicleState(JavaPlugin plugin) {
        this.bypassPermissionEnabled = plugin.getConfig().getBoolean("allow-bypass.enabled", false);
    }

    @EventHandler
    public void onRide(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player && event.getVehicle() instanceof LivingEntity) {
            Player player = (Player) event.getEntered();

            if (bypassPermissionEnabled) {
                if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
            }

            LivingEntity vehicle = (LivingEntity) event.getVehicle();

            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

            List<EntityData> metadata = new ArrayList<>();
            metadata.add(new EntityData(EntityMetadataIndex.HEALTH, EntityDataTypes.FLOAT, (float) vehicle.getHealth()));
            user.writePacket(new WrapperPlayServerEntityMetadata(vehicle.getEntityId(), metadata));

            vehicles.put(player, vehicle.getEntityId());
        }
    }

    @EventHandler
    public void onExitRide(VehicleExitEvent event) {
        if (event.getExited() instanceof Player && event.getVehicle() instanceof LivingEntity) {
            Player player = (Player) event.getExited();

            if (bypassPermissionEnabled) {
                if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
            }

            LivingEntity vehicle = (LivingEntity) event.getVehicle();

            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

            List<EntityData> metadata = new ArrayList<>();
            metadata.add(new EntityData(EntityMetadataIndex.HEALTH, EntityDataTypes.FLOAT, 0.5f));
            user.writePacket(new WrapperPlayServerEntityMetadata(vehicle.getEntityId(), metadata));

            vehicles.remove(player, vehicle.getEntityId());
        }
    }
}