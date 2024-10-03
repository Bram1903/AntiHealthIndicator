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
    private final ConfigManager<P> configManager;
    private final LogManager<P> logManager;

    public CacheManager(AHIPlatform<P> platform) {
        this.cache = new ConcurrentHashMap<>();

        this.platform = platform;
        this.logManager = platform.getLogManager();
        this.configManager = platform.getConfigManager();

        LogCacheStats();

        this.platform.getLogManager().debug("CacheManager initialized.");
    }

    public Map<Integer, CachedEntity> getUserCache(@NonNull UUID uuid) {
        return this.cache.computeIfAbsent(uuid, u -> new ConcurrentHashMap<>());
    }

    public void removeUserCache(@NonNull UUID uuid) {
        this.cache.remove(uuid);
    }

    public Optional<CachedEntity> getCachedEntity(@NonNull UUID uuid, int entityId) {
        return Optional.ofNullable(getUserCache(uuid).get(entityId));
    }

    public Optional<RidableEntity> getVehicleData(@NonNull UUID uuid, int entityId) {
        return getCachedEntity(uuid, entityId)
                .filter(entityData -> entityData instanceof RidableEntity)
                .map(entityData -> (RidableEntity) entityData);
    }

    public void addLivingEntity(@NonNull UUID uuid, int entityId, @NonNull CachedEntity cachedEntity) {
        getUserCache(uuid).put(entityId, cachedEntity);
    }

    public void removeEntity(@NonNull UUID uuid, int entityId) {
        getUserCache(uuid).remove(entityId);
    }

    public void resetUserCache(@NonNull UUID uuid) {
        getUserCache(uuid).clear();
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
        return getUserCache(uuid).entrySet().stream()
                .filter(entry -> entry.getValue() instanceof RidableEntity
                        && ((RidableEntity) entry.getValue()).getPassengerId() == passengerId)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
    }

    private void LogCacheStats() {
        platform.getScheduler().runAsyncTaskAtFixedRate((o) -> {
            if (!configManager.getSettings().isDebug()) return;

            ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, CachedEntity>> cacheMap = cache;

            int underlyingSize = cacheMap.values().stream().mapToInt(Map::size).sum();
            int avgCacheSizePerUser = cacheMap.isEmpty() ? 0 : Math.floorDiv(underlyingSize, cacheMap.size());

            Component statsComponent = Component.text()
                    .append(Component.text("[DEBUG] Cache Stats", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .appendNewline()
                    .append(Component.text("\n\u25cf User Cache Size: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(cache.size(), NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Average Cache Size Per User: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(avgCacheSizePerUser, NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Underlying Cache Size: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(underlyingSize, NamedTextColor.AQUA))
                    .build();

            platform.broadcastComponent(statsComponent, "AntiHealthIndicator.Debug");
        }, 10, 10, TimeUnit.SECONDS);
    }
}