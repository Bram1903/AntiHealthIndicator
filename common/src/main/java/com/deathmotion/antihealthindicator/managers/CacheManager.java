package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.data.VehicleData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {

    private final ConcurrentHashMap<Integer, EntityType> entityType = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, VehicleData> vehicleData = new ConcurrentHashMap<>();

    public void addEntity(int entityId, EntityType entityType) {
        this.entityType.putIfAbsent(entityId, entityType);
    }

    public void addVehicleData(int entityId, VehicleData vehicleData) {
        this.vehicleData.put(entityId, vehicleData);
    }

    public void removeEntity(int entityId) {
        this.entityType.remove(entityId);
    }

    public void updateVehicleHealth(int entityId, float health) {
        VehicleData vehicleData = this.vehicleData.get(entityId);
        if (vehicleData == null) {
            return;
        }
        vehicleData.setHealth(health);
    }

    public void updateVehiclePassenger(int entityId, int passengerId) {
        VehicleData vehicleData = this.vehicleData.get(entityId);
        if (vehicleData == null) {
            return;
        }
        vehicleData.setPassengerId(passengerId);
    }

    public float getVehicleHealth(int entityId) {
        VehicleData vehicleData = this.vehicleData.get(entityId);
        if (vehicleData == null) {
            return 0;
        }
        return vehicleData.getHealth();
    }

    public EntityType getEntityTypeById(int entityId) {
        return this.entityType.get(entityId);
    }

    public boolean isUserPassenger(int entityId, int userId) {
        VehicleData vehicleData = this.vehicleData.get(entityId);
        if (vehicleData == null) {
            return false;
        }

        return vehicleData.getPassengerId() == userId;
    }
}