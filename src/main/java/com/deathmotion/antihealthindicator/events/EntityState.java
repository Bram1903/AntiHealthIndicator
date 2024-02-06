package com.deathmotion.antihealthindicator.events;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

import java.util.List;

/**
 * This class listens to entity events, such as spawning, dying, loading and unloading.
 */
public class EntityState implements Listener {
    private final CacheManager cacheManager;

    public EntityState(AntiHealthIndicator plugin) {
        this.cacheManager = plugin.getCacheManager();
    }

    /**
     * Handles the EntitySpawnEvent, creating a new instance of EntityDataMap for the spawned entity.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void entitySpawn(EntitySpawnEvent event) {
        createEntityDataMap(event.getEntity());
    }

    /**
     * Handles the EntitiesLoadEvent, creating a new instance of EntityDataMap for each of the loaded entities.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void entityLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            createEntityDataMap(entity);
        }
    }

    /**
     * Handles the PlayerJoinEvent, creating a new instance of EntityDataMap for the joined player.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event) {
        createEntityDataMap(event.getPlayer());
    }

    /**
     * Handles the EntityDeathEvent, removing the died entity from the entityDataMap.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void entityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

        cacheManager.removeEntityFromCache(event.getEntity().getEntityId());
    }

    /**
     * Handles the EntitiesUnloadEvent, removing the unloaded entity from the entityDataMap.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void entityUnload(EntitiesUnloadEvent event) {
        List<Entity> entityList = event.getEntities();
        for (Entity entity : entityList) {
            if (entity instanceof LivingEntity) {
                cacheManager.removeEntityFromCache(entity.getEntityId());
            }
        }
    }

    /**
     * Handles the PlayerQuitEvent, removing the quit player from the entityDataMap.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        this.cacheManager.removeEntityFromCache(event.getPlayer().getEntityId());
    }

    /**
     * Create a map of EntityDataMap instances.
     *
     * @param entity the entity for which the map to be created
     */
    private void createEntityDataMap(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        this.cacheManager.addEntityToCache(entity);
    }
}