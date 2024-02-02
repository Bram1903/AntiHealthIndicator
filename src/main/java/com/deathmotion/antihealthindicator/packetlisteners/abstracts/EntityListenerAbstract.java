package com.deathmotion.antihealthindicator.packetlisteners.abstracts;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.entity.*;

import java.util.List;

public abstract class EntityListenerAbstract extends PacketListenerAbstract {
    private final ConfigManager configManager;
    private final CacheManager cacheManager;


    public EntityListenerAbstract(AntiHealthIndicator plugin) {
        cacheManager = plugin.getCacheManager();
        configManager = plugin.getConfigManager();
    }

    @Override
    public abstract void onPacketSend(PacketSendEvent event);

    /**
     * Handle a packet being sent to the user.
     * The method first identifies if the user is the packetEntityId,
     * or if the user is contained within the current list of vehicles, filtering them out if either is true.
     * Subsequently, it checks the entity's type and conditionally return
     * if it fits a set of defined criteria such as if the entity is a tamed wolf owned by the user,
     * or if the entity is an instance of a Wither or EnderDragon.
     * Lastly,
     * it modifies the metadata of any LivingEntity types based on a set of predetermined conditions
     * as provided in the spoofLivingEntityMetadata()
     * method.
     *
     * @param player         The player to whom the packet is being sent.
     * @param packetEntityId The ID linked with an entity in the packet.
     * @param entityMetadata The list of metadata associated with the current entity.
     */
    protected void handlePacket(Player player, int packetEntityId, List<EntityData> entityMetadata) {
        if (player.getEntityId() == packetEntityId) return;

        if (configManager.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED)) {
            if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
        }

        if (configManager.getConfigurationOption(ConfigOption.IGNORE_VEHICLES_ENABLED)) {
            if (cacheManager.isPlayerVehicleInCache(player.getUniqueId(), packetEntityId)) return;
        }

        Entity entity = getEntityDataById(packetEntityId);
        if (entity == null) return;

        if (entity instanceof Wither || entity instanceof EnderDragon) {
            return;
        }

        if (entity instanceof Wolf && configManager.getConfigurationOption(ConfigOption.IGNORE_WOLVES_ENABLED)) {
            if (shouldIgnoreWolf(player, (Wolf) entity)) return;
        }

        if (entity instanceof IronGolem && configManager.getConfigurationOption(ConfigOption.IGNORE_IRON_GOLEMS_ENABLED)) {
            if (!configManager.getConfigurationOption(ConfigOption.GRADUAL_IRON_GOLEM_HEALTH_ENABLED)) return;

            entityMetadata.forEach(this::spoofIronGolemMetadata);
            return;
        }

        if (entity instanceof LivingEntity) {
            entityMetadata.forEach(this::spoofLivingEntityMetadata);
        }

        if (entity instanceof Player) {
            entityMetadata.forEach(this::spoofPlayerMetadata);
        }
    }

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
    public Entity getEntityDataById(int entityId) {
        if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_18)) {
            return SpigotReflectionUtil.getEntityById(entityId);
        }

        return cacheManager.getEntityFromCache(entityId);
    }

    private boolean shouldIgnoreWolf(Player player, Wolf wolf) {
        boolean ignoreTamedWolves = configManager.getConfigurationOption(ConfigOption.FOR_TAMED_WOLVES_ENABLED);
        boolean ignoreOwnedWolves = configManager.getConfigurationOption(ConfigOption.FOR_OWNED_WOLVES_ENABLED);

        if (!ignoreTamedWolves && !ignoreOwnedWolves) {
            return true;
        }

        if (ignoreTamedWolves && wolf.isTamed()) {
            return true;
        }

        if (ignoreOwnedWolves) {
            return wolf.isTamed() && wolf.getOwner() != null && wolf.getOwner().getUniqueId().equals(player.getUniqueId());
        }

        return false;
    }

    /**
     * This method sets the health of the Iron Golem based on its current health value.
     * It will adjust the health to the highest possible value within the range
     * that the current health falls into.
     * This is done so the correct health texture is displayed on the client.
     *
     * @param obj the Iron Golem entity data.
     */
    private void spoofIronGolemMetadata(EntityData obj) {
        // Checks if the metadata index is related to air ticks and if the configuration option for it is enabled
        if (obj.getIndex() == MetadataIndex.AIR_TICKS && configManager.getConfigurationOption(ConfigOption.AIR_TICKS_ENABLED)) {
            // Sets a dynamic value for air ticks
            setDynamicValue(obj, 1);
        }
        // Checks if the metadata index is related to health and if the configuration option for it is enabled
        if (obj.getIndex() == MetadataIndex.HEALTH && configManager.getConfigurationOption(ConfigOption.HEALTH_ENABLED)) {
            // Retrieves the current health of the Iron Golem
            float health = (float) obj.getValue();

            // Adjusts the Iron Golem's health based on its current health range.
            if (health > 74) {
                obj.setValue(100f);
            } else if (health <= 74 && health > 49) {
                obj.setValue(74f);
            } else if (health <= 49 && health > 24) {
                obj.setValue(49f);
            } else if (health <= 24 && health > 0) {
                obj.setValue(24f);
            }
        }
    }

    private void spoofLivingEntityMetadata(EntityData obj) {
        if (obj.getIndex() == MetadataIndex.AIR_TICKS && configManager.getConfigurationOption(ConfigOption.AIR_TICKS_ENABLED)) {
            setDynamicValue(obj, 1);
        }
        if (obj.getIndex() == MetadataIndex.HEALTH && configManager.getConfigurationOption(ConfigOption.HEALTH_ENABLED)) {
            obj.setValue(0.5f);
        }
    }

    private void spoofPlayerMetadata(EntityData obj) {
        if (obj.getIndex() == MetadataIndex.ABSORPTION && configManager.getConfigurationOption(ConfigOption.ABSORPTION_ENABLED)) {
            setDynamicValue(obj, 0);
        }
        if (obj.getIndex() == MetadataIndex.XP && configManager.getConfigurationOption(ConfigOption.XP_ENABLED)) {
            setDynamicValue(obj, 0);
        }
    }

    /**
     * This method is designed to handle and set dynamic values for different types of data objects.
     * This is necessary because the value of the data object is stored as an Object, and not as a primitive type.
     * Besides, the value of the data object is not always an integer,
     * but can be a float, double, long, short or byte, etc.
     * <p>
     * The reason why I am not simply using a byte since my value will never be bigger than zero or one is because
     * legacy versions for some retarded reason don't support upcasting of bytes to integers.
     *
     * @param obj        The EntityData containing the value to be set.
     * @param spoofValue The value to be set in the EntityData object.
     */
    private void setDynamicValue(EntityData obj, int spoofValue) {
        Object value = obj.getValue();

        if (value instanceof Integer) {
            obj.setValue(spoofValue);
        } else if (value instanceof Short) {
            obj.setValue((short) spoofValue);
        } else if (value instanceof Byte) {
            obj.setValue((byte) spoofValue);
        } else if (value instanceof Long) {
            obj.setValue((long) spoofValue);
        } else if (value instanceof Float) {
            obj.setValue((float) spoofValue);
        } else if (value instanceof Double) {
            obj.setValue((double) spoofValue);
        }
    }
}