package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.schedulers.ServerScheduler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {

    private final ConcurrentHashMap<Integer, Entity> entityDataMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> vehicles = new ConcurrentHashMap<>();
    ServerScheduler scheduler;

    public CacheManager(AntiHealthIndicator plugin) {
        this.scheduler = plugin.getScheduler();
    }

    public void cacheLivingEntityData() {
        scheduler.runTask(null, () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof LivingEntity) {
                        addEntityToCache(entity);
                    }
                }
            }
        });
    }

    public void addEntityToCache(Entity entity) {
        entityDataMap.putIfAbsent(entity.getEntityId(), entity);
    }

    public Entity getEntityFromCache(int entityId) {
        return entityDataMap.get(entityId);
    }

    public void removeEntityFromCache(int entityId) {
        entityDataMap.remove(entityId);
    }

    public void addVehicleToCache(UUID playerUniqueId, Integer entityId) {
        vehicles.putIfAbsent(playerUniqueId, entityId);
    }

    public boolean isPlayerVehicleInCache(UUID playerUniqueId, Integer vehicleId) {
        return vehicles.containsKey(playerUniqueId) && vehicles.get(playerUniqueId).equals(vehicleId);
    }

    public void removeVehicle(UUID playerUniqueId) {
        vehicles.remove(playerUniqueId);
    }
}