package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.EntityData;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager<P> {

    private final AHIPlatform<P> platform;

    private final ConcurrentHashMap<Integer, EntityData> entityData = new ConcurrentHashMap<>();

    public CacheManager(AHIPlatform<P> platform) {
        this.platform = platform;
    }

    public void cacheEntityData(int entityId, EntityData entityData) {
        this.entityData.putIfAbsent(entityId, entityData);
    }
}