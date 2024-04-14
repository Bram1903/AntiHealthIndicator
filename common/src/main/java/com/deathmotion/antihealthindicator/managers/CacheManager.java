package com.deathmotion.antihealthindicator.managers;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {

    private final ConcurrentHashMap<Integer, EntityType> entityData = new ConcurrentHashMap<>();

    public void addEntity(int entityId, EntityType entityType) {
        this.entityData.putIfAbsent(entityId, entityType);
    }

    public void removeEntity(int entityId) {
        this.entityData.remove(entityId);
    }

    public EntityType getEntityDataById(int entityId) {
        return this.entityData.get(entityId);
    }
}