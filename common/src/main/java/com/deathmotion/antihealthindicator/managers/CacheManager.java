/*
 *
 *  * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  * Copyright (C) 2024 Bram and contributors
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.data.LivingEntityData;
import com.deathmotion.antihealthindicator.data.RidableEntityData;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {
    private final ConcurrentHashMap<Integer, LivingEntityData> livingEntityDataCache = new ConcurrentHashMap<>();

    public Optional<LivingEntityData> getLivingEntityData(int entityId) {
        return Optional.ofNullable(livingEntityDataCache.get(entityId));
    }

    public Optional<RidableEntityData> getVehicleData(int entityId) {
        Optional<LivingEntityData> livingEntityData = getLivingEntityData(entityId);

        if (livingEntityData.isPresent()) {
            LivingEntityData entityData = livingEntityData.get();
            if (entityData instanceof RidableEntityData) {
                return Optional.of((RidableEntityData) entityData);
            }
        }

        return Optional.empty();
    }

    public boolean isLivingEntityCached(int entityId) {
        return livingEntityDataCache.containsKey(entityId);
    }

    public void addLivingEntity(int entityId, LivingEntityData livingEntityData) {
        livingEntityDataCache.putIfAbsent(entityId, livingEntityData);
    }

    public void removeLivingEntity(int entityId) {
        livingEntityDataCache.remove(entityId);
    }

    public void updateVehiclePassenger(int entityId, int passengerId) {
        getVehicleData(entityId).ifPresent(ridableEntityData -> ridableEntityData.setPassengerId(passengerId));
    }

    public float getVehicleHealth(int entityId) {
        return getVehicleData(entityId).map(RidableEntityData::getHealth).orElse(0f);
    }

    public boolean isUserPassenger(int entityId, int userId) {
        return getVehicleData(entityId).map(ridableEntityData -> ridableEntityData.getPassengerId() == userId).orElse(false);
    }

    public int getPassengerId(int entityId) {
        return getVehicleData(entityId).map(RidableEntityData::getPassengerId).orElse(0);
    }

    public int getEntityIdByPassengerId(int passengerId) {
        return livingEntityDataCache.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof RidableEntityData && ((RidableEntityData) entry.getValue()).getPassengerId() == passengerId)
                .mapToInt(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
    }
}