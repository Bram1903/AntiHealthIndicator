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
import com.deathmotion.antihealthindicator.data.VehicleData;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {
    private final ConcurrentHashMap<Integer, LivingEntityData> livingEntityDataCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, VehicleData> vehicleDataCache = new ConcurrentHashMap<>();

    public Optional<LivingEntityData> getLivingEntityData(int entityId) {
        return Optional.ofNullable(livingEntityDataCache.get(entityId));
    }

    public Optional<VehicleData> getVehicleData(int entityId) {
        return Optional.ofNullable(vehicleDataCache.get(entityId));
    }

    public boolean isLivingEntityCached(int entityId) {
        return livingEntityDataCache.containsKey(entityId);
    }

    public void addLivingEntity(int entityId, LivingEntityData livingEntityData) {
        livingEntityDataCache.putIfAbsent(entityId, livingEntityData);
    }

    public void addVehicleData(int entityId, VehicleData vehicleData) {
        vehicleDataCache.put(entityId, vehicleData);
    }

    public void removeLivingEntity(int entityId) {
        livingEntityDataCache.remove(entityId);
        vehicleDataCache.remove(entityId);
    }

    public void updateVehicleHealth(int entityId, float health) {
        getVehicleData(entityId).ifPresent(vehicleData -> vehicleData.setHealth(health));
    }

    public void updateVehiclePassenger(int entityId, int passengerId) {
        getVehicleData(entityId).ifPresent(vehicleData -> vehicleData.setPassengerId(passengerId));
    }

    public float getVehicleHealth(int entityId) {
        return getVehicleData(entityId).map(VehicleData::getHealth).orElse(0f);
    }

    public boolean isUserPassenger(int entityId, int userId) {
        return getVehicleData(entityId).map(vehicleData -> vehicleData.getPassengerId() == userId).orElse(false);
    }

    public int getPassengerId(int entityId) {
        return getVehicleData(entityId).map(VehicleData::getPassengerId).orElse(0);
    }

    public int getEntityIdByPassengerId(int passengerId) {
        return vehicleDataCache.entrySet().stream()
                .filter(entry -> entry.getValue().getPassengerId() == passengerId)
                .mapToInt(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
    }
}