/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.cache.LivingEntityData;
import com.deathmotion.antihealthindicator.data.cache.RidableEntityData;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Manages the cache services of the platform.
 *
 * @param <P> The platform type.
 */
@Getter
public class CacheManager<P> implements RemovalListener<Integer, LivingEntityData> {
    private final AHIPlatform<P> platform;
    private final Cache<Integer, LivingEntityData> cache;

    /**
     * Constructs a new CacheManager with the specified {@link AHIPlatform}.
     *
     * @param platform The platform to use.
     */
    public CacheManager(AHIPlatform<P> platform) {
        this.platform = platform;
        Caffeine<Integer, LivingEntityData> cacheBuilder = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .removalListener(this);

        if (platform.getConfigurationOption(ConfigOption.DEBUG_ENABLED)) {
            cacheBuilder.recordStats();
            LogCacheStats();
        }

        this.cache = cacheBuilder.build();
        this.platform.getLogManager().debug("CacheManager initialized.");
    }

    /**
     * Gets the LivingEntity data if present in cache.
     *
     * @param entityId The unique identifier of the entity
     * @return Optional of LivingEntityData
     */
    public Optional<LivingEntityData> getLivingEntityData(int entityId) {
        return Optional.ofNullable(cache.getIfPresent(entityId));
    }

    /**
     * Gets the RidableEntity data if present in cache.
     *
     * @param entityId The unique identifier of the vehicle
     * @return Optional of RidableEntityData
     */
    public Optional<RidableEntityData> getVehicleData(int entityId) {
        return getLivingEntityData(entityId).filter(entityData -> entityData instanceof RidableEntityData)
                .map(entityData -> (RidableEntityData) entityData);
    }

    /**
     * Adds a LivingEntity to the cache.
     *
     * @param entityId         The unique identifier of the entity
     * @param livingEntityData The data of the LivingEntity
     */
    public void addLivingEntity(int entityId, LivingEntityData livingEntityData) {
        cache.put(entityId, livingEntityData);
    }

    /**
     * Updates the passenger of the vehicle (RidableEntity).
     *
     * @param entityId    The unique identifier of the vehicle
     * @param passengerId The unique identifier of the passenger
     */
    public void updateVehiclePassenger(int entityId, int passengerId) {
        getVehicleData(entityId).ifPresent(ridableEntityData -> ridableEntityData.setPassengerId(passengerId));
    }

    /**
     * Returns the health of the vehicle (RidableEntity).
     *
     * @param entityId The unique identifier of the vehicle
     * @return A float value representing the health of the vehicle
     */
    public float getVehicleHealth(int entityId) {
        return getVehicleData(entityId).map(RidableEntityData::getHealth).orElse(0.5f);
    }

    /**
     * Checks if the user is a passenger of a vehicle.
     *
     * @param entityId The unique identifier of the vehicle
     * @param userId   The unique identifier of the user
     * @return A boolean indicating if the user is a passenger of the vehicle
     */
    public boolean isUserPassenger(int entityId, int userId) {
        return getVehicleData(entityId).map(ridableEntityData -> ridableEntityData.getPassengerId() == userId).orElse(false);
    }

    /**
     * Returns the passenger id of a vehicle.
     *
     * @param entityId The unique identifier of the vehicle
     * @return An integer representing the passenger id of the vehicle
     */
    public int getPassengerId(int entityId) {
        return getVehicleData(entityId).map(RidableEntityData::getPassengerId).orElse(0);
    }

    /**
     * Returns the entity id associated with the specified passenger id.
     *
     * @param passengerId The unique identifier of the passenger
     * @return An integer representing the entity id of the vehicle
     */
    public int getEntityIdByPassengerId(int passengerId) {
        for (Map.Entry<Integer, LivingEntityData> entry : cache.asMap().entrySet()) {
            if (entry.getValue() instanceof RidableEntityData && ((RidableEntityData) entry.getValue()).getPassengerId() == passengerId) {
                return entry.getKey();
            }
        }
        return 0;
    }

    /**
     * This function is called after an entry has been removed from the cache.
     *
     * @param key   The unique identifier of the entity
     * @param value The data of the LivingEntity
     * @param cause The cause of removal
     */
    @Override
    public void onRemoval(Integer key, LivingEntityData value, @NotNull RemovalCause cause) {
        if (key == null || !cause.wasEvicted()) {
            return;
        }

        platform.getScheduler().runAsyncTask((o) -> {
            if (platform.isEntityRemoved(key, null)) return;
            addLivingEntity(key, value);
        });
    }

    /**
     * Logs cache stats at a regular interval.
     */
    private void LogCacheStats() {
        platform.getScheduler().runAsyncTaskAtFixedRate((o) -> {
            CacheStats newStats = cache.stats();

            Component statsComponent = Component.text()
                    .append(Component.text("[DEBUG] Cache Stats", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .appendNewline()
                    .append(Component.text("\n\u25cf Cache Size: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(cache.estimatedSize(), NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Evicted Items: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(newStats.evictionCount(), NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Hit Count: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(newStats.hitCount(), NamedTextColor.AQUA))
                    .build();

            platform.broadcastComponent(statsComponent, "AntiHealthIndicator.Debug");
        }, 10, 10, TimeUnit.SECONDS);
    }
}