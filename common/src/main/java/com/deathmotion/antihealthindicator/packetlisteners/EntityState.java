package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.VehicleData;
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
    private final CacheManager cacheManager;

    public EntityState(AHIPlatform<P> platform) {
        this.cacheManager = platform.getCacheManager();
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
        int[] passengers = packet.getPassengers();

        if (passengers.length > 0) {
            int vehicleId = packet.getEntityId();

            this.cacheManager.updateVehiclePassenger(vehicleId, passengers[0]);
            handlePassengerEvent(user, vehicleId, this.cacheManager.getVehicleHealth(vehicleId));
        }
    }

    private void handleEntityDestroy(WrapperPlayServerDestroyEntities packet) {
        int[] entityIds = packet.getEntityIds();

        for (int entityId : entityIds) {
            this.cacheManager.removeEntity(entityId);
        }
    }

    private void handlePassengerEvent(User user, int vehicleId, float healthValue) {
        List<EntityData> metadata = new ArrayList<>();
        metadata.add(new EntityData(MetadataIndex.HEALTH, EntityDataTypes.FLOAT, healthValue));
        user.writePacket(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
    }
}