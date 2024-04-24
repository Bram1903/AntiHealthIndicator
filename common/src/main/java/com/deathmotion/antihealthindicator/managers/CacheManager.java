/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.cache.LivingEntityData;
import com.deathmotion.antihealthindicator.data.cache.RidableEntityData;
import com.github.benmanes.caffeine.cache.*;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Getter
public class CacheManager<P> {
    private final AHIPlatform<P> platform;

    private final Cache<Integer, LivingEntityData> livingEntityDataCache;

    public CacheManager(AHIPlatform<P> platform) {
        this.platform = platform;

        this.livingEntityDataCache = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .removalListener((RemovalListener<Integer, LivingEntityData>) (key, value, cause) -> {
                    if (key != null && cause.wasEvicted()) {
                        this.checkDeletedEntity(key, value);
                    }
                })
                .build();
    }

    public Optional<LivingEntityData> getLivingEntityData(int entityId) {
        return Optional.ofNullable(livingEntityDataCache.getIfPresent(entityId));
    }

    public Optional<RidableEntityData> getVehicleData(int entityId) {
        return getLivingEntityData(entityId).filter(entityData -> entityData instanceof RidableEntityData)
                .map(entityData -> (RidableEntityData) entityData);
    }

    public boolean isLivingEntityCached(int entityId) {
        return livingEntityDataCache.asMap().containsKey(entityId);
    }

    public void addLivingEntity(int entityId, LivingEntityData livingEntityData) {
        livingEntityDataCache.put(entityId, livingEntityData);
    }

    public void removeLivingEntity(int entityId) {
        livingEntityDataCache.invalidate(entityId);
    }

    public void updateVehiclePassenger(int entityId, int passengerId) {
        getVehicleData(entityId).ifPresent(ridableEntityData -> ridableEntityData.setPassengerId(passengerId));
    }

    public float getVehicleHealth(int entityId) {
        return getVehicleData(entityId).map(RidableEntityData::getHealth).orElse(0.5f);
    }

    public boolean isUserPassenger(int entityId, int userId) {
        return getVehicleData(entityId).map(ridableEntityData -> ridableEntityData.getPassengerId() == userId).orElse(false);
    }

    public int getPassengerId(int entityId) {
        return getVehicleData(entityId).map(RidableEntityData::getPassengerId).orElse(0);
    }

    public int getEntityIdByPassengerId(int passengerId) {
        return livingEntityDataCache.asMap().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof RidableEntityData && ((RidableEntityData) entry.getValue()).getPassengerId() == passengerId)
                .mapToInt(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
    }

    private void checkDeletedEntity(int entityId, LivingEntityData livingEntityData) {
        if (platform.isEntityRemoved(entityId, null)) {
            this.platform.getLoggerWrapper().info("Entity with id " + entityId + " was removed from the world, removing from cache");
        }
        else {
            this.platform.getLoggerWrapper().info("Entity with id " + entityId + " was not removed from the world, re-adding to cache");
            this.addLivingEntity(entityId, livingEntityData);
        }
    }
}