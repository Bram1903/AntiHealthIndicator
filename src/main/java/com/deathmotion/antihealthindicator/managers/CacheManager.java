package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.data.WolfData;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CacheManager {

    private final AntiHealthIndicator plugin;

    private final ConcurrentHashMap<Integer, Entity> entityData = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> vehicles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, WolfData> wolfData = new ConcurrentHashMap<>();

    public CacheManager(AntiHealthIndicator plugin) {
        this.plugin = plugin;
    }

    public void cacheLivingEntityData() {
        FoliaCompatUtil.runTask(this.plugin, (Object unused) -> {
            for (World world : Bukkit.getWorlds()) {
                for (LivingEntity livingEntity : world.getLivingEntities()) {
                    entityData.putIfAbsent(livingEntity.getEntityId(), livingEntity);

                    if (livingEntity instanceof Wolf) {
                        addWolfDataToCache(livingEntity.getEntityId(), (Wolf) livingEntity);
                    }
                }
            }
        });
    }

    public void addEntityToCache(Entity entity) {
        entityData.putIfAbsent(entity.getEntityId(), entity);
    }

    public void addWolfDataToCache(Integer entityId, Wolf wolf) {
        wolfData.computeIfAbsent(entityId, key -> {
            WolfData wolfData = new WolfData();

            wolfData.setTamed(wolf.isTamed());

            if (wolf.isTamed() && wolf.getOwner() != null) {
                wolfData.setOwnerUniqueId(wolf.getOwner().getUniqueId());
            }

            return wolfData;
        });
    }

    public Entity getEntityFromCache(int entityId) {
        return entityData.get(entityId);
    }

    public WolfData getWolfDataFromCache(int entityId) {
        return wolfData.get(entityId);
    }

    public void updateWolfDataInCache(int entityId, WolfData wolfData) {
        this.wolfData.put(entityId, wolfData);
    }

    public void removeEntityFromCache(int entityId) {
        entityData.remove(entityId);
    }

    public void removeWolfDataFromCache(int entityId) {
        wolfData.remove(entityId);
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