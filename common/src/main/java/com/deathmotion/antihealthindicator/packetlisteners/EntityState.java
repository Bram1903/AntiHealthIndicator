package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.LivingEntityData;
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
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.wrapper.play.server.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        if (PacketType.Play.Server.JOIN_GAME == type) {
            handleJoinGame(new WrapperPlayServerJoinGame(event));
            return;
        }

        if (PacketType.Play.Server.ENTITY_METADATA == type) {
            handleEntityMetadata(new WrapperPlayServerEntityMetadata(event), event.getUser());
        }

        if (PacketType.Play.Server.SET_PASSENGERS == type) {
            handleSetPassengers(new WrapperPlayServerSetPassengers(event), event.getUser());
            return;
        }

        if (PacketType.Play.Server.ATTACH_ENTITY == type) {
            handleAttachEntity(new WrapperPlayServerAttachEntity(event), event.getUser());
            return;
        }

        if (PacketType.Play.Server.DESTROY_ENTITIES == type) {
            handleEntityDestroy(new WrapperPlayServerDestroyEntities(event), event.getPlayer());
        }
    }

    private void handleSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet) {
        EntityType entityType = packet.getEntityType();

        LivingEntityData livingEntityData = new LivingEntityData();
        livingEntityData.setEntityType(entityType);

        this.cacheManager.addLivingEntity(packet.getEntityId(), livingEntityData);

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.ABSTRACT_HORSE)) {
            this.cacheManager.addVehicleData(packet.getEntityId(), new VehicleData());
        }
    }

    private void handleSpawnEntity(WrapperPlayServerSpawnEntity packet) {
        EntityType entityType = packet.getEntityType();

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.LIVINGENTITY)) {
            LivingEntityData livingEntityData = new LivingEntityData();
            livingEntityData.setEntityType(entityType);

            this.cacheManager.addLivingEntity(packet.getEntityId(), livingEntityData);

            if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.ABSTRACT_HORSE)) {
                this.cacheManager.addVehicleData(packet.getEntityId(), new VehicleData());
            }
        }
    }

    private void handleJoinGame(WrapperPlayServerJoinGame packet) {
        LivingEntityData livingEntityData = new LivingEntityData();
        livingEntityData.setEntityType(EntityTypes.PLAYER);

        this.cacheManager.addLivingEntity(packet.getEntityId(), livingEntityData);
    }

    private void handleEntityMetadata(WrapperPlayServerEntityMetadata packet, User user) {
        int entityId = packet.getEntityId();

        LivingEntityData livingEntityData = this.cacheManager.getLivingEntityData(entityId).orElse(null);
        if (livingEntityData == null || livingEntityData.getEntityType() != EntityTypes.WOLF) return;

        packet.getEntityMetadata().forEach(wolfEntityData -> {
            if (wolfEntityData.getIndex() == MetadataIndex.TAMABLE_TAMED) {
                livingEntityData.setTamed(((Byte) wolfEntityData.getValue() & 0x04) != 0);
            } else if (wolfEntityData.getIndex() == MetadataIndex.TAMABLE_OWNER) {
                Object value = wolfEntityData.getValue();

                UUID ownerUUID = value instanceof String
                        ? Optional.ofNullable((String) value)
                        .filter(user.getUUID().toString()::equals)
                        .map(UUID::fromString)
                        .orElse(null)
                        : ((Optional<UUID>) value)
                        .filter(user.getUUID()::equals)
                        .orElse(null);

                livingEntityData.setOwnerUUID(ownerUUID);
            }
        });
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

        if (entityId > 0) {
            this.cacheManager.updateVehiclePassenger(entityId, passengerId);
            handlePassengerEvent(user, entityId, this.cacheManager.getVehicleHealth(entityId), true);
        } else {
            // With the Entity Attach packet, the entity ID is set to -1 when the entity is detached;
            // Thus we need to retrieve the vehicle we stepped of by using a reverse lookup by passenger ID
            int reversedEntityId = this.cacheManager.getEntityIdByPassengerId(passengerId);
            this.cacheManager.updateVehiclePassenger(reversedEntityId, -1);

            if (user.getEntityId() == passengerId) {
                handlePassengerEvent(user, reversedEntityId, 0.5F, false);
            }
        }
    }

    private void handleEntityDestroy(WrapperPlayServerDestroyEntities packet, Object player) {
        int[] entityIds = packet.getEntityIds();

        for (int entityId : entityIds) {
            if (this.platform.isEntityRemoved(entityId, player)) {
                this.cacheManager.removeLivingEntity(entityId);
            }
        }
    }

    private void handlePassengerEvent(User user, int vehicleId, float healthValue, boolean entering) {
        List<EntityData> metadata = new ArrayList<>();

        if (!entering) {
            if (isBypassEnabled) {
                if (this.platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass")) return;
            }
        }

        metadata.add(new EntityData(MetadataIndex.HEALTH, EntityDataTypes.FLOAT, healthValue));
        user.sendPacketSilently(new WrapperPlayServerEntityMetadata(vehicleId, metadata));
    }
}