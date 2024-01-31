package com.deathmotion.antihealthindicator.events;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class listens to entity events, such as spawning, dying, loading and unloading.
 */
public class EntityState implements Listener {
    private final AntiHealthIndicator instance = AntiHealthIndicator.getInstance();

    // Map that stores entity data.
    private final ConcurrentHashMap<Integer, Entity> entityDataMap = instance.getEntityDataMap();

    /**
     * Handles the EntitySpawnEvent, creating a new instance of EntityDataMap for the spawned entity.
     *
     * @param event the event
     */
    @EventHandler
    public void entitySpawn(EntitySpawnEvent event) {
        createEntityDataMap(event.getEntity());
    }

    /**
     * Handles the EntitiesLoadEvent, creating a new instance of EntityDataMap for each of the loaded entities.
     *
     * @param event the event
     */
    @EventHandler
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
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        createEntityDataMap(event.getPlayer());
    }

    /**
     * Handles the EntityDeathEvent, removing the died entity from the entityDataMap.
     *
     * @param event the event
     */
    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

        entityDataMap.remove(event.getEntity().getEntityId());
    }

    /**
     * Handles the EntitiesUnloadEvent, removing the unloaded entity from the entityDataMap.
     *
     * @param event the event
     */
    @EventHandler
    public void entityUnload(EntitiesUnloadEvent event) {
        List<Entity> entityList = event.getEntities();
        for (Entity entity : entityList) {
            if (entity instanceof LivingEntity) {
                entityDataMap.remove(entity.getEntityId());
            }
        }
    }

    /**
     * Handles the PlayerQuitEvent, removing the quit player from the entityDataMap.
     *
     * @param event the event
     */
    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        entityDataMap.remove(event.getPlayer().getEntityId());
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

        this.entityDataMap.putIfAbsent(entity.getEntityId(), entity);
    }
}