package com.deathmotion.antihealthindicator.util.entity;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.entity.Entity;

public class EntityUtil {

    /**
     * Retrieves an Entity object by identifier.
     * <p>
     * If the server version is older than 1.18, it will use the `SpigotReflectionUtils#getEntityById` method.
     * Otherwise, it will use the AntiHealthIndicator's instance method to get the entity data
     * from the entityDataMap.
     * If an entity with the provided entityId does not exist, it
     * will return null.
     *
     * @param entityId Identifier of the entity to retrieve
     * @return Entity object if found, otherwise null
     */
    public static Entity getEntityDataById(int entityId) {
        if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_18)) {
            return SpigotReflectionUtil.getEntityById(entityId);
        }

        AntiHealthIndicator instance = AntiHealthIndicator.getInstance();
        if (instance.getEntityDataMap().containsKey(entityId)) {
            return instance.getEntityDataMap().get(entityId);
        }

        return null; // Return null if the entity data is not found
    }
}