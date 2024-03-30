package com.deathmotion.antihealthindicator.packetlisteners.abstracts;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.deathmotion.antihealthindicator.data.WolfData;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.entity.*;

import java.util.List;
import java.util.UUID;

public abstract class EntityListenerAbstract extends PacketListenerAbstract {
    private final ConfigManager configManager;
    private final CacheManager cacheManager;

    private final boolean useEntityCache;
    private final boolean healthTexturesSupported;
    private final boolean allowBypassEnabled;
    private final boolean ignoreVehiclesEnabled;
    private final boolean ignoreWolvesEnabled;
    private final boolean ignoreTamedWolves;
    private final boolean ignoreOwnedWolves;
    private final boolean ignoreIronGolemsEnabled;
    private final boolean gradualIronGolemHealthEnabled;

    public EntityListenerAbstract(AntiHealthIndicator plugin) {
        cacheManager = plugin.getCacheManager();
        configManager = plugin.getConfigManager();

        useEntityCache = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18);
        healthTexturesSupported = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_15);
        allowBypassEnabled = configManager.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
        ignoreVehiclesEnabled = configManager.getConfigurationOption(ConfigOption.IGNORE_VEHICLES_ENABLED);
        ignoreWolvesEnabled = configManager.getConfigurationOption(ConfigOption.IGNORE_WOLVES_ENABLED);
        ignoreTamedWolves = configManager.getConfigurationOption(ConfigOption.FOR_TAMED_WOLVES_ENABLED);
        ignoreOwnedWolves = configManager.getConfigurationOption(ConfigOption.FOR_OWNED_WOLVES_ENABLED);
        ignoreIronGolemsEnabled = configManager.getConfigurationOption(ConfigOption.IGNORE_IRON_GOLEMS_ENABLED);
        gradualIronGolemHealthEnabled = configManager.getConfigurationOption(ConfigOption.GRADUAL_IRON_GOLEM_HEALTH_ENABLED);
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

        if (allowBypassEnabled) {
            if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
        }

        if (ignoreVehiclesEnabled) {
            if (cacheManager.isPlayerVehicleInCache(player.getUniqueId(), packetEntityId)) return;
        }

        Entity entity = getEntityDataById(packetEntityId);
        if (entity == null) return;

        if (entity instanceof Wither || entity instanceof EnderDragon) {
            return;
        }

        if (entity instanceof Wolf && ignoreWolvesEnabled) {
            if (shouldIgnoreWolf(player, (Wolf) entity)) return;
        }

        if (entity instanceof IronGolem && ignoreIronGolemsEnabled) {
            if (!gradualIronGolemHealthEnabled || !healthTexturesSupported) {
                entityMetadata.forEach(this::spoofLivingEntityMetadata);
                return;
            }

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

    /**
     * Determines if a wolf should be ignored based on the player and wolf metadata.
     * The method first checks whether ignoring tamed and owned wolves is enabled
     * in the configuration.
     * If both are disabled, all wolves are ignored.
     * Then, it checks whether the entity cache is being used.
     * If so, and if the wolf data found in the cache is not null,
     * the method applies the same rules as before but based on the cached data.
     * If not, or if the wolf data is null, it applies the same rules based on the actual wolf entity data.
     *
     * @param player The player entity to check the ownership of the wolf against.
     * @param wolf   The wolf entity to check if it should be ignored.
     * @return true, if the wolf should be ignored, otherwise false.
     */
    private boolean shouldIgnoreWolf(Player player, Wolf wolf) {
        boolean isTamed;
        UUID ownerUniqueId;

        if (!ignoreTamedWolves && !ignoreOwnedWolves) {
            return true;
        }

        if (useEntityCache) {
            WolfData wolfData = cacheManager.getWolfDataFromCache(wolf.getEntityId());
            if (wolfData != null) {
                isTamed = wolfData.isTamed();
                ownerUniqueId = wolfData.getOwnerUniqueId();
            } else {
                return false;
            }
        } else {
            isTamed = wolf.isTamed();
            ownerUniqueId = wolf.getOwner() != null ? wolf.getOwner().getUniqueId() : null;
        }

        return (ignoreTamedWolves && isTamed) || (ignoreOwnedWolves && isTamed && ownerUniqueId != null
                && ownerUniqueId.equals(player.getUniqueId()));
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

    /**
     * This method modifies the metadata of a LivingEntity.
     * <p>
     * For air ticks metadata, it sets a dynamic air ticks value to 1 if the configuration option for it is enabled.
     * For health metadata, it sets health to 0.5 if the configuration option for it is enabled.
     *
     * @param obj the Living Entity's data.
     */
    private void spoofLivingEntityMetadata(EntityData obj) {
        if (obj.getIndex() == MetadataIndex.AIR_TICKS && configManager.getConfigurationOption(ConfigOption.AIR_TICKS_ENABLED)) {
            setDynamicValue(obj, 1);
        }
        if (obj.getIndex() == MetadataIndex.HEALTH && configManager.getConfigurationOption(ConfigOption.HEALTH_ENABLED)) {
            if (((Float) obj.getValue()) > 0) {
                obj.setValue(0.5f);
            }
        }
    }

    /**
     * This method modifies the metadata of a Player.
     * <p>
     * For absorption metadata, it sets absorption to 0 if the configuration option for it is enabled.
     * For XP metadata, it sets XP to 0 if the configuration option for it is enabled.
     *
     * @param obj the Player's data.
     */
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