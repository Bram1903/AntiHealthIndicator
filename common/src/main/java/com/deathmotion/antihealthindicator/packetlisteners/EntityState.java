package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.EntityDataStore;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.wrappers.PlatformLoggerWrapperImpl;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityState<P> extends PacketListenerAbstract {
    private final CacheManager cacheManager;
    private final PlatformLoggerWrapperImpl logger;

    public EntityState(AHIPlatform<P> platform) {
        this.cacheManager = platform.getCacheManager();
        this.logger = platform.getLoggerWrapper();
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

        if (PacketType.Play.Server.SPAWN_PLAYER == type) {
            handleSpawnPlayer(new WrapperPlayServerSpawnPlayer(event));
            return;
        }

        if (PacketType.Play.Server.ENTITY_METADATA == type) {
            handleEntityMetadata(new WrapperPlayServerEntityMetadata(event));
            return;
        }

        if (PacketType.Play.Server.DESTROY_ENTITIES == type) {
            handleEntityDestroy(new WrapperPlayServerDestroyEntities(event));
        }
    }

    private void handleSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet) {
        int entityId = packet.getEntityId();

        EntityType entityType = packet.getEntityType();
        EntityDataStore entityData = new EntityDataStore();
        entityData.setEntityType(entityType);

        EntityDataStore updatedEntityData = handleWolfMetaData(entityData, entityType, packet.getEntityMetadata());

        this.cacheManager.addEntity(entityId, updatedEntityData);
    }

    private void handleSpawnEntity(WrapperPlayServerSpawnEntity packet) {
        EntityType entityType = packet.getEntityType();

        if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.LIVINGENTITY)) {
            EntityDataStore entityData = new EntityDataStore();
            entityData.setEntityType(entityType);

            this.cacheManager.addEntity(packet.getEntityId(), entityData);
        }
    }

    private void handleSpawnPlayer(WrapperPlayServerSpawnPlayer packet) {
        EntityDataStore entityData = new EntityDataStore();
        entityData.setEntityType(EntityTypes.PLAYER);

        this.cacheManager.addEntity(packet.getEntityId(), entityData);
    }

    private void handleEntityMetadata(WrapperPlayServerEntityMetadata packet) {
        int entityId = packet.getEntityId();
        EntityDataStore entityData = this.cacheManager.getEntityDataById(entityId);
        if (entityData == null) return;

        EntityType entityType = entityData.getEntityType();
        EntityDataStore updatedEntityData = handleWolfMetaData(entityData, entityType, packet.getEntityMetadata());

        this.cacheManager.updateEntity(entityId, updatedEntityData);
    }

    private EntityDataStore handleWolfMetaData(EntityDataStore entityData, EntityType entityType, List<EntityData> entityMetadata) {
        if (entityType == EntityTypes.WOLF) {
            entityMetadata.forEach(wolfEntityData -> {
                if (wolfEntityData.getIndex() == 17) {
                    entityData.setTamed(((Byte) wolfEntityData.getValue() & 0x04) != 0);
                }

                if (wolfEntityData.getIndex() == 18) {
                    Optional<UUID> ownerUUID = (Optional<UUID>) wolfEntityData.getValue();

                    ownerUUID.ifPresent(entityData::setOwnerUUID);
                }
            });
        }

        return entityData;
    }

    private void handleEntityDestroy(WrapperPlayServerDestroyEntities packet) {
        int[] entityIds = packet.getEntityIds();

        for (int entityId : entityIds) {
            this.cacheManager.removeEntity(entityId);
        }
    }
}