/*
 *
 *  * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  * Copyright (C) 2024 Bram and contributors
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.deathmotion.antihealthindicator.packetlisteners.spoofers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.LivingEntityData;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;

import java.util.List;

public class EntityMetadataListener<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final CacheManager cacheManager;

    private final boolean healthTexturesSupported;
    private final boolean allowBypassEnabled;
    private final boolean ignoreVehiclesEnabled;
    private final boolean ignoreWolvesEnabled;
    private final boolean ignoreTamedWolves;
    private final boolean ignoreOwnedWolves;
    private final boolean ignoreIronGolemsEnabled;
    private final boolean gradualIronGolemHealthEnabled;
    private final boolean healthEnabled;
    private final boolean airTicksEnabled;
    private final boolean absorptionEnabled;
    private final boolean xpEnabled;

    public EntityMetadataListener(AHIPlatform<P> platform) {
        this.platform = platform;
        this.cacheManager = platform.getCacheManager();

        healthTexturesSupported = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_15);
        allowBypassEnabled = platform.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
        ignoreVehiclesEnabled = platform.getConfigurationOption(ConfigOption.IGNORE_VEHICLES_ENABLED);
        ignoreWolvesEnabled = platform.getConfigurationOption(ConfigOption.IGNORE_WOLVES_ENABLED);
        ignoreTamedWolves = platform.getConfigurationOption(ConfigOption.FOR_TAMED_WOLVES_ENABLED);
        ignoreOwnedWolves = platform.getConfigurationOption(ConfigOption.FOR_OWNED_WOLVES_ENABLED);
        ignoreIronGolemsEnabled = platform.getConfigurationOption(ConfigOption.IGNORE_IRON_GOLEMS_ENABLED);
        gradualIronGolemHealthEnabled = platform.getConfigurationOption(ConfigOption.GRADUAL_IRON_GOLEM_HEALTH_ENABLED);
        healthEnabled = platform.getConfigurationOption(ConfigOption.HEALTH_ENABLED);
        airTicksEnabled = platform.getConfigurationOption(ConfigOption.AIR_TICKS_ENABLED);
        absorptionEnabled = platform.getConfigurationOption(ConfigOption.ABSORPTION_ENABLED);
        xpEnabled = platform.getConfigurationOption(ConfigOption.XP_ENABLED);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) return;

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
        User user = event.getUser();
        List<EntityData> entityMetadataList = packet.getEntityMetadata();

        if (event.getUser().getEntityId() == packet.getEntityId()) return;

        if (allowBypassEnabled) {
            if (platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Bypass")) return;
        }

        if (ignoreVehiclesEnabled) {
            if (this.cacheManager.isUserPassenger(packet.getEntityId(), user.getEntityId())) return;
        }

        int entityId = packet.getEntityId();

        LivingEntityData livingEntityData = this.cacheManager.getLivingEntityData(entityId).orElse(null);
        if (livingEntityData == null) return;
        EntityType entityType = livingEntityData.getEntityType();

        if (entityType == EntityTypes.WITHER || entityType == EntityTypes.ENDER_DRAGON) {
            return;
        }

        if (entityType == EntityTypes.WOLF && ignoreWolvesEnabled) {
            if (shouldIgnoreWolf(user, livingEntityData)) return;
        }

        entityMetadataList.forEach(entityData -> {
            if (EntityTypes.isTypeInstanceOf(entityType, EntityTypes.ABSTRACT_HORSE)) {
                if (entityData.getIndex() == MetadataIndex.HEALTH) {
                    // Update the health in our cache
                    // to send the proper health of a vehicle when entering another method
                    this.cacheManager.updateVehicleHealth(entityId, (float) entityData.getValue());
                }
            }

            if (entityType == EntityTypes.IRON_GOLEM && ignoreIronGolemsEnabled) {
                if (!gradualIronGolemHealthEnabled || !healthTexturesSupported) {
                    spoofLivingEntityMetadata(entityData);
                } else {
                    spoofIronGolemMetadata(entityData);
                }

                return;
            }

            spoofLivingEntityMetadata(entityData);

            if (entityType == EntityTypes.PLAYER) {
                spoofPlayerMetadata(entityData);
            }
        });

        event.markForReEncode(true);
    }

    private boolean shouldIgnoreWolf(User user, LivingEntityData livingEntityData) {
        if (!ignoreTamedWolves && !ignoreOwnedWolves) {
            return true;
        }

        return (ignoreTamedWolves && livingEntityData.isTamed()) || (ignoreOwnedWolves && livingEntityData.isOwnerPresent() && livingEntityData.getOwnerUUID().equals(user.getUUID()));
    }

    private void spoofIronGolemMetadata(EntityData obj) {
        // Checks if the metadata index is related to air ticks and if the configuration option for it is enabled
        if (obj.getIndex() == MetadataIndex.AIR_TICKS && airTicksEnabled) {
            // Sets a dynamic value for air ticks
            setDynamicValue(obj, 1);
        }
        // Checks if the metadata index is related to health and if the configuration option for it is enabled
        if (obj.getIndex() == MetadataIndex.HEALTH && healthEnabled) {
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
        if (obj.getIndex() == MetadataIndex.AIR_TICKS && airTicksEnabled) {
            setDynamicValue(obj, 1);
        }
        if (obj.getIndex() == MetadataIndex.HEALTH && healthEnabled) {
            if (((Float) obj.getValue()) > 0) {
                obj.setValue(0.5f);
            }
        }
    }

    private void spoofPlayerMetadata(EntityData obj) {
        if (obj.getIndex() == MetadataIndex.ABSORPTION && absorptionEnabled) {
            setDynamicValue(obj, 0);
        }
        if (obj.getIndex() == MetadataIndex.XP && xpEnabled) {
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