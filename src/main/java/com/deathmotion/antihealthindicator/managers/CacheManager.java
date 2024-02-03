package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {

    private final AntiHealthIndicator plugin;

    private final ConcurrentHashMap<Integer, Entity> entityDataMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> vehicles = new ConcurrentHashMap<>();

    public CacheManager(AntiHealthIndicator plugin) {
        this.plugin = plugin;
    }

    public void cacheLivingEntityData() {
        FoliaCompatUtil.runTask(this.plugin, (Object unused) -> {
            for (World world : Bukkit.getWorlds()) {
                for (LivingEntity livingEntity : world.getLivingEntities()) {
                    entityDataMap.putIfAbsent(livingEntity.getEntityId(), livingEntity);
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