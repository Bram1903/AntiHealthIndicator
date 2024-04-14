package com.deathmotion.antihealthindicator.managers;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {

    private final ConcurrentHashMap<Integer, EntityType> entityType = new ConcurrentHashMap<>();

    public void addEntity(int entityId, EntityType entityType) {
        this.entityType.putIfAbsent(entityId, entityType);
    }

    public void removeEntity(int entityId) {
        this.entityType.remove(entityId);
    }

    public EntityType getEntityTypeById(int entityId) {
        return this.entityType.get(entityId);
    }
}