package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.VehicleData;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.*;

import java.util.ArrayList;
import java.util.List;

public class EntityState<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final CacheManager cacheManager;

    private final boolean isBypassEnabled;

    public EntityState(AHIPlatform<P> platform) {
        this.platform = platform;
        this.cacheManager = platform.getCacheManager();

        this.isBypassEnabled = platform.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        final PacketTypeCommon type = event.getPacketType();

        if (PacketType.Play.Server.SPAWN_LIVING_ENTITY == type) {
            handleSpawnLivingEntity(new WrapperPlayServerSpawnLivingEntity(event));
            return;
        }

        if (PacketType.Play.Server.SPAWN_ENTITY == type) {
            handleSpawnEntity(new WrapperPlayServerSpawnEntity(event));
            return;
        }

        if (PacketType.Play.Server.SET_PASSENGERS == type) {
            handleSetPassengers(new WrapperPlayServerSetPassengers(event), event.getUser());
            return;
        }

        if (PacketType.Play.Server.ATTACH_ENTITY == type) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event), event.getUser());
        }

        if (PacketType.Play.Server.DESTROY_ENTITIES == type) {
            handleEntityDestroy(new WrapperPlayServerDestroyEntities(event));
        }
    }

    private void handleSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet) {
        EntityType entityType = packet.getEntityType();

        this.cacheManager.addEntity(packet.getEntityId(), entityType);

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.ABSTRACT_HORSE)) {
            this.cacheManager.addVehicleData(packet.getEntityId(), new VehicleData());
        }
    }

    private void handleSpawnEntity(WrapperPlayServerSpawnEntity packet) {
        EntityType entityType = packet.getEntityType();

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.LIVINGENTITY)) {
            this.cacheManager.addEntity(packet.getEntityId(), entityType);

            if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.ABSTRACT_HORSE)) {
                this.cacheManager.addVehicleData(packet.getEntityId(), new VehicleData());
            }
        }
    }

    private void handleSetPassengers(WrapperPlayServerSetPassengers packet, User user) {
        int entityId = packet.getEntityId();
        int[] passengers = packet.getPassengers();

        if (passengers.length > 0) {
            this.cacheManager.updateVehiclePassenger(entityId, passengers[0]);
            handlePassengerEvent(user, entityId, this.cacheManager.getVehicleHealth(entityId), true);
        } else {
            int passengerId = this.cacheManager.getPassengerId(entityId);
            this.cacheManager.updateVehiclePassenger(entityId, -1);

            if (user.getEntityId() == passengerId) {
                handlePassengerEvent(user, entityId, 0.5F, false);
            }
        }
    }

    private void handleAttachEntity(WrapperPlayServerAttachEntity packet, User user) {
        int entityId = packet.getHoldingId();
        int passengerId = packet.getAttachedId();

        System.out.println("Horse ID: " + entityId);
        System.out.println("Passenger ID: " + passengerId);
        System.out.println("User Entity ID: " + user.getEntityId());

        if (entityId > 0) {
            this.cacheManager.updateVehiclePassenger(entityId, passengerId);
            handlePassengerEvent(user, entityId, this.cacheManager.getVehicleHealth(entityId), true);
        }
        else {
            this.cacheManager.updateVehiclePassenger(entityId, -1);

            if (user.getEntityId() == passengerId) {
                handlePassengerEvent(user, entityId, 0.5F, false);
            }
        }
    }

    private void handleEntityDestroy(WrapperPlayServerDestroyEntities packet) {
        int[] entityIds = packet.getEntityIds();

        for (int entityId : entityIds) {
            this.cacheManager.removeEntity(entityId);
        }
    }

    private void handlePassengerEvent(User user, int vehicleId, float healthValue, boolean entering) {
        if (entering) {
            List<EntityData> metadata = new ArrayList<>();
            metadata.add(new EntityData(MetadataIndex.HEALTH, EntityDataTypes.FLOAT, healthValue));
            user.writePacket(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
        } else {
            if (isBypassEnabled) {
                this.platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass");
            }

            List<EntityData> metadata = new ArrayList<>();
            metadata.add(new EntityData(MetadataIndex.HEALTH, EntityDataTypes.FLOAT, healthValue));
            user.sendPacketSilently(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
        }
    }
}