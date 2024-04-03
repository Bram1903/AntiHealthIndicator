package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.EntityDataStore;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.*;

import java.util.UUID;

public class EntityState<P> extends PacketListenerAbstract {
    private final CacheManager<P> cacheManager;

    public EntityState(AHIPlatform<P> platform) {
        this.cacheManager = platform.getCacheManager();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        final PacketTypeCommon type = event.getPacketType();

        if (PacketType.Play.Server.SPAWN_LIVING_ENTITY == type) {
            handleSpawnLivingEntity(new WrapperPlayServerSpawnLivingEntity(event));
        }

        if (PacketType.Play.Server.SPAWN_PLAYER == type) {
            handleSpawnPlayer(new WrapperPlayServerSpawnPlayer(event));
        }

        if (PacketType.Play.Server.ENTITY_METADATA == type) {
            handleEntityMetadata(new WrapperPlayServerEntityMetadata(event));
        }

        if (PacketType.Play.Server.DESTROY_ENTITIES == type) {
            handleEntityDestroy(new WrapperPlayServerDestroyEntities(event));
        }
    }

    private void handleSpawnLivingEntity(WrapperPlayServerSpawnLivingEntity packet) {
        EntityType entityType = packet.getEntityType();
        EntityDataStore entityData = new EntityDataStore();
        entityData.setEntityType(entityType);

        if (entityType == EntityTypes.WOLF) {
            packet.getEntityMetadata().forEach(wolfEntityData -> {
                if (wolfEntityData.getIndex() == 17) {
                    entityData.setTamed(((Byte) wolfEntityData.getValue() & 0x04) != 0);
                }

                if (wolfEntityData.getIndex() == 18) {
                    entityData.setOwnerUUID((UUID) wolfEntityData.getValue());
                }
            });

            this.cacheManager.addEntity(packet.getEntityId(), entityData);
        } else {
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

        if (entityType == EntityTypes.WOLF) {
            packet.getEntityMetadata().forEach(wolfEntityData -> {
                if (wolfEntityData.getIndex() == 17) {
                    entityData.setTamed(((Byte) wolfEntityData.getValue() & 0x04) != 0);
                }

                if (wolfEntityData.getIndex() == 18) {
                    entityData.setOwnerUUID((UUID) wolfEntityData.getValue());
                }
            });
        }
    }

    private void handleEntityDestroy(WrapperPlayServerDestroyEntities packet) {
        int[] entityIds = packet.getEntityIds();

        for (int entityId : entityIds) {
            this.cacheManager.removeEntity(entityId);
        }
    }
}