package com.deathmotion.antihealthindicator.packetlisteners.impl.abstracts;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.util.entity.EntityMetadataIndex;
import com.deathmotion.antihealthindicator.util.entity.EntityUtil;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityListenerAbstract extends PacketListenerAbstract {
    protected final boolean bypassPermissionEnabled;
    protected final boolean spoofAirTicks;
    protected final boolean spoofHealth;
    protected final boolean spoofAbsorption;
    protected final boolean spoofXp;
    protected final boolean ignoreVehicles;
    protected final boolean ignoreWolves;
    protected final boolean ignoreTamedWolves;
    protected final boolean ignoreOwnedWolves;


    public EntityListenerAbstract(JavaPlugin plugin) {
        this.bypassPermissionEnabled = plugin.getConfig().getBoolean("allow-bypass.enabled", false);
        this.spoofAirTicks = plugin.getConfig().getBoolean("spoof.entity-data.air-ticks.enabled", true);
        this.spoofHealth = plugin.getConfig().getBoolean("spoof.entity-data.health.enabled", true);
        this.spoofAbsorption = plugin.getConfig().getBoolean("spoof.entity-data.absorption.enabled", true);
        this.spoofXp = plugin.getConfig().getBoolean("spoof.entity-data.spoof-xp.enabled", true);
        this.ignoreVehicles = plugin.getConfig().getBoolean("spoof.entity-data.health.ignore-vehicles", true);
        this.ignoreWolves = plugin.getConfig().getBoolean("spoof.entity-data.health.ignore-wolves.enabled", true);
        this.ignoreTamedWolves = plugin.getConfig().getBoolean("spoof.entity-data.health.ignore-tamed-wolves.enabled", false);
        this.ignoreOwnedWolves = plugin.getConfig().getBoolean("spoof.entity-data.health.ignore-owned-wolves.enabled", true);
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

        if (bypassPermissionEnabled) {
            if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
        }

        if (ignoreVehicles) {
            ConcurrentHashMap<Player, Integer> vehicles = AntiHealthIndicator.getInstance().getVehicles();
            if (vehicles.containsKey(player) && vehicles.get(player) == packetEntityId) return;
        }

        Entity entity = EntityUtil.getEntityDataById(packetEntityId);
        if (entity == null) return;

        if (ignoreWolves && entity instanceof Wolf) {
            if (shouldIgnoreWolf(player, (Wolf) entity)) return;
        }

        if (entity instanceof Wither || entity instanceof EnderDragon) {
            return;
        }

        if (entity instanceof LivingEntity) {
            entityMetadata.forEach(this::spoofLivingEntityMetadata);
        }

        if (entity instanceof Player) {
            entityMetadata.forEach(this::spoofPlayerMetadata);
        }
    }

    private boolean shouldIgnoreWolf(Player player, Wolf wolf) {
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

    private void spoofLivingEntityMetadata(EntityData obj) {
        if (obj.getIndex() == EntityMetadataIndex.AIR_TICKS && spoofAirTicks) {
            setDynamicValue(obj, 1);
        }
        if (obj.getIndex() == EntityMetadataIndex.HEALTH && spoofHealth) {
            obj.setValue(0.5f);
        }
    }

    private void spoofPlayerMetadata(EntityData obj) {
        if (obj.getIndex() == EntityMetadataIndex.ABSORPTION && spoofAbsorption) {
            setDynamicValue(obj, 0);
        }
        if (obj.getIndex() == EntityMetadataIndex.XP && spoofXp) {
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