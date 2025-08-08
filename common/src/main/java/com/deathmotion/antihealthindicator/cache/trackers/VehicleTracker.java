package com.deathmotion.antihealthindicator.cache.trackers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.cache.EntityCache;
import com.deathmotion.antihealthindicator.cache.entities.CachedEntity;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.deathmotion.antihealthindicator.models.Settings;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;

import java.util.Arrays;
import java.util.Collections;

public class VehicleTracker {
    private final AHIPlayer player;
    private final EntityCache cache;
    private final ConfigManager<?> configManager;

    public VehicleTracker(AHIPlayer player, EntityCache cache) {
        this.player = player;
        this.cache = cache;
        this.configManager = AHIPlatform.getInstance().getConfigManager();
    }

    public void onPacketSend(PacketSendEvent event) {
        Settings settings = configManager.getSettings();
        if (!settings.getEntityData().isEnabled() || settings.getEntityData().isPlayersOnly()) {
            return;
        }

        PacketTypeCommon type = event.getPacketType();
        if (type == PacketType.Play.Server.SET_PASSENGERS) {
            handleSetPassengers(new WrapperPlayServerSetPassengers(event));
        } else if (type == PacketType.Play.Server.ATTACH_ENTITY) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event));
        }
    }

    private void handleSetPassengers(WrapperPlayServerSetPassengers packet) {
        int vehicleId = packet.getEntityId();
        if (!shouldProcess(vehicleId)) return;

        int[] passengers = packet.getPassengers();

        if (passengers.length > 0) {
            if (Arrays.stream(passengers).anyMatch(passenger -> passenger == player.user.getEntityId())) {
                if (cache.getCurrentVehicleId().map(currentVehicleId -> currentVehicleId != vehicleId).orElse(true)) {
                    updateState(vehicleId, true);
                }
            } else {
                cache.getCurrentVehicleId().ifPresent(currentVehicleId -> {
                    if (currentVehicleId == vehicleId) {
                        updateState(vehicleId, false);
                    }
                });
            }
        } else {
            cache.getCurrentVehicleId().ifPresent(currentVehicleId -> {
                if (currentVehicleId == vehicleId) {
                    updateState(vehicleId, false);
                }
            });
        }
    }

    private void handleAttachEntity(WrapperPlayServerAttachEntity packet) {
        int vehicleId = packet.getHoldingId();
        int passenger = packet.getAttachedId();

        if (passenger != player.user.getEntityId()) {
            return;
        }

        // attaching to a vehicle (>0) is enter; holdingId==0 is detach
        if (vehicleId > 0) {
            if (!shouldProcess(vehicleId)) return;
            updateState(vehicleId, true);
        } else {
            // Detach from current vehicle (if any)
            cache.getCurrentVehicleId().ifPresent(currentVehicleId -> {
                if (!shouldProcess(currentVehicleId)) return;
                updateState(currentVehicleId, false);
            });
        }
    }

    private void updateState(int vehicleId, boolean entering) {
        CachedEntity cachedEntity = cache.getEntity(vehicleId);
        if (cachedEntity == null) return;

        if (entering) {
            cache.setCurrentVehicleId(vehicleId);
        } else {
            cache.setCurrentVehicleId(null);
        }

        sendMetadata(vehicleId, entering ? cachedEntity.getHealth() : 0.5F);
    }

    private boolean shouldProcess(int entityId) {
        // Don't try to treat the player as their own vehicle
        return entityId != player.user.getEntityId();
    }

    private void sendMetadata(final int vehicleId, final float health) {
        AHIPlatform.getInstance().getLogManager().info("Spoofing vehicle metadata for vehicle ID: " + vehicleId + " (Type: " + cache.getEntity(vehicleId).getEntityType().getName().getKey() + ") with health: " + health);

        AHIPlatform.getInstance().getScheduler().runAsyncTask(task ->
                player.user.sendPacketSilently(
                        new WrapperPlayServerEntityMetadata(
                                vehicleId,
                                Collections.singletonList(new EntityData<>(
                                        player.metadataIndex.HEALTH,
                                        EntityDataTypes.FLOAT,
                                        health
                                ))
                        )
                )
        );
    }
}
