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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Getter
public class CacheManager<P> {
    private final AHIPlatform<P> platform;
    private final Cache<Integer, LivingEntityData> livingEntityDataCache;
    private final RemovalListener<Integer, LivingEntityData> removalListener;

    public CacheManager(AHIPlatform<P> platform) {
        this.platform = platform;

        this.removalListener = (key, value, cause) -> {
            if (key != null && cause.wasEvicted()) {
                this.platform.getScheduler().runAsyncTask((o) -> {
                    if (!this.platform.isEntityRemoved(key, null)) {
                        this.addLivingEntity(key, value);
                    }
                });
            }
        };

        this.livingEntityDataCache = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .removalListener(this.removalListener)
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
        return livingEntityDataCache.getIfPresent(entityId) != null;
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
        for (Map.Entry<Integer, LivingEntityData> entry : livingEntityDataCache.asMap().entrySet()) {
            if (entry.getValue() instanceof RidableEntityData && ((RidableEntityData) entry.getValue()).getPassengerId() == passengerId) {
                return entry.getKey();
            }
        }
        return 0;
    }
}