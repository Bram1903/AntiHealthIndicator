package com.deathmotion.antihealthindicator.cache;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.schedulers.ServerScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

/**
 * Cache manager responsible for managing caches.
 */
public class CacheManager {

    /**
     * Cache the data of all living entities in all worlds.
     * Entities are cached in a map where key is entity's ID and value is an object of EntityDataMap.
     * If entity data is already cached, it won't be overwritten.
     * This method schedules the task to run in Bukkit's main thread.
     */
    public void cacheLivingEntityData() {
        // Get the singleton instance of AntiHealthIndicator
        ServerScheduler scheduler = AntiHealthIndicator.getInstance().getScheduler();

        // Get the map responsible for storing Entity data
        Map<Integer, Entity> entityDataMap = AntiHealthIndicator.getInstance().getEntityDataMap();

        scheduler.runTask(null, () -> {
            // Iterate over all worlds managed by the Bukkit API
            for (World world : Bukkit.getWorlds()) {
                // Iterate over all entities within the current world
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof LivingEntity) {
                        // Add the entity to the cache, only if it hasn't been cached earlier
                        entityDataMap.putIfAbsent(entity.getEntityId(), entity);
                    }
                }
            }
        });
    }
}