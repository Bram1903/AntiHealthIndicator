package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.data.VehicleData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {
    private final ConcurrentHashMap<Integer, EntityType> entityTypeCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, VehicleData> vehicleDataCache = new ConcurrentHashMap<>();

    public void addEntity(int entityId, EntityType entityType) {
        entityTypeCache.putIfAbsent(entityId, entityType);
    }

    public void addVehicleData(int entityId, VehicleData vehicleData) {
        vehicleDataCache.put(entityId, vehicleData);
    }

    public void removeEntity(int entityId) {
        entityTypeCache.remove(entityId);
        vehicleDataCache.remove(entityId);
    }

    public Optional<VehicleData> getVehicleData(int entityId) {
        return Optional.ofNullable(vehicleDataCache.get(entityId));
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

    public EntityType getEntityTypeById(int entityId) {
        return entityTypeCache.get(entityId);
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