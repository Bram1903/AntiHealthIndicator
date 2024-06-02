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
import com.deathmotion.antihealthindicator.data.cache.CachedEntity;
import com.deathmotion.antihealthindicator.data.cache.RidableEntity;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages the cache services of the platform.
 *
 * @param <P> The platform type.
 */
@Getter
public class CacheManager<P> {
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, CachedEntity>> cache;
    private final AHIPlatform<P> platform;
    private final LogManager<P> logManager;

    public CacheManager(AHIPlatform<P> platform) {
        this.cache = new ConcurrentHashMap<>();
        this.platform = platform;
        this.logManager = platform.getLogManager();

        if (platform.getConfigurationOption(ConfigOption.DEBUG_ENABLED)) {
            LogCacheStats();
        }

        this.platform.getLogManager().debug("CacheManager initialized.");
    }

    public Optional<CachedEntity> getCachedEntity(@NonNull UUID uuid, int entityId) {
        ConcurrentHashMap<Integer, CachedEntity> entityMap = cache.get(uuid);
        return Optional.ofNullable(entityMap != null ? entityMap.get(entityId) : null);
    }

    public Optional<RidableEntity> getVehicleData(@NonNull UUID uuid, int entityId) {
        return getCachedEntity(uuid, entityId).filter(entityData -> entityData instanceof RidableEntity)
                .map(entityData -> (RidableEntity) entityData);
    }

    public void addLivingEntity(@NonNull UUID uuid, int entityId, @NonNull CachedEntity cachedEntity) {
        cache.compute(uuid, (key, entityMap) -> {
            if (entityMap == null) {
                entityMap = new ConcurrentHashMap<>();
            }
            entityMap.putIfAbsent(entityId, cachedEntity);
            return entityMap;
        });
    }

    public void removeUser(@NonNull UUID uuid) {
        logManager.debug("Entity removed from cache: " + uuid);
        cache.remove(uuid);
    }

    public void removeEntity(@NonNull UUID uuid, int entityId) {
        cache.computeIfPresent(uuid, (key, entityMap) -> {
            entityMap.remove(entityId);
            //logManager.debug("Entity removed from cache: " + entityId);
            return entityMap;
        });
    }

    public void updateVehiclePassenger(@NonNull UUID uuid, int entityId, int passengerId) {
        getVehicleData(uuid, entityId).ifPresent(ridableEntityData -> ridableEntityData.setPassengerId(passengerId));
    }

    public float getVehicleHealth(@NonNull UUID uuid, int entityId) {
        return getVehicleData(uuid, entityId).map(RidableEntity::getHealth).orElse(0.5f);
    }

    public boolean isUserPassenger(@NonNull UUID uuid, int entityId, int userId) {
        return getVehicleData(uuid, entityId).map(ridableEntityData -> ridableEntityData.getPassengerId() == userId).orElse(false);
    }

    public int getPassengerId(@NonNull UUID uuid, int entityId) {
        return getVehicleData(uuid, entityId).map(RidableEntity::getPassengerId).orElse(0);
    }

    public int getEntityIdByPassengerId(@NonNull UUID uuid, int passengerId) {
        return cache.getOrDefault(uuid, new ConcurrentHashMap<>()).entrySet().stream()
                .filter(entry -> entry.getValue() instanceof RidableEntity
                        && ((RidableEntity) entry.getValue()).getPassengerId() == passengerId)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
    }

    private void LogCacheStats() {
        platform.getScheduler().runAsyncTaskAtFixedRate((o) -> {
            int totalKeys = cache.size();
            int totalValues = cache.values().stream().mapToInt(ConcurrentHashMap::size).sum();

            double averageCacheSizePerEntry = totalKeys != 0 ? (double) totalValues / totalKeys : 0;

            Component statsComponent = Component.text()
                    .append(Component.text("[DEBUG] Cache Stats", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .appendNewline()
                    .append(Component.text("\n\u25cf Cache Entries: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(totalKeys, NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Total Cache Size: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(totalValues, NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Average Cache Size Per Entry: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(averageCacheSizePerEntry, NamedTextColor.AQUA))
                    .build();

            platform.broadcastComponent(statsComponent, "AntiHealthIndicator.Debug");
        }, 10, 10, TimeUnit.SECONDS);
    }
}