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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages the cache services of the platform.
 *
 * @param <P> The platform type.
 */
@Getter
public class CacheManager<P> extends SimplePacketListenerAbstract {
    private final Cache<User, Cache<Integer, CachedEntity>> cache;

    private final AHIPlatform<P> platform;
    private final LogManager<P> logManager;
    private final boolean debugEnabled;

    public CacheManager(AHIPlatform<P> platform) {
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
                .weakKeys()
                .ticker(Ticker.systemTicker());

        this.platform = platform;
        this.logManager = platform.getLogManager();
        this.debugEnabled = platform.getConfigurationOption(ConfigOption.DEBUG_ENABLED);

        if (debugEnabled) {
            cacheBuilder.recordStats();
            LogCacheStats();
        }

        this.cache = cacheBuilder.build();
        this.platform.getLogManager().debug("CacheManager initialized.");
    }

    public Cache<Integer, CachedEntity> getUserCache(@NonNull User user) {
        return cache.get(user, u -> Caffeine.newBuilder().ticker(Ticker.systemTicker()).build());
    }

    public Optional<CachedEntity> getCachedEntity(User user, int entityId) {
        return Optional.ofNullable(getUserCache(user).getIfPresent(entityId));
    }

    public Optional<RidableEntity> getVehicleData(User user, int entityId) {
        return getCachedEntity(user, entityId)
                .filter(entityData -> entityData instanceof RidableEntity)
                .map(entityData -> (RidableEntity) entityData);
    }

    public void addLivingEntity(User user, int entityId, CachedEntity cachedEntity) {
        getUserCache(user).put(entityId, cachedEntity);
    }

    public void removeEntity(User user, int entityId) {
        getUserCache(user).invalidate(entityId);
    }

    public void updateVehiclePassenger(User user, int entityId, int passengerId) {
        getVehicleData(user, entityId).ifPresent(ridableEntityData -> ridableEntityData.setPassengerId(passengerId));
    }

    public float getVehicleHealth(User user, int entityId) {
        return getVehicleData(user, entityId).map(RidableEntity::getHealth).orElse(0.5f);
    }

    public boolean isUserPassenger(User user, int entityId, int userId) {
        return getVehicleData(user, entityId).map(ridableEntityData -> ridableEntityData.getPassengerId() == userId).orElse(false);
    }

    public int getPassengerId(User user, int entityId) {
        return getVehicleData(user, entityId).map(RidableEntity::getPassengerId).orElse(0);
    }

    public int getEntityIdByPassengerId(User user, int passengerId) {
        return getUserCache(user).asMap().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof RidableEntity
                        && ((RidableEntity) entry.getValue()).getPassengerId() == passengerId)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
    }

    private void LogCacheStats() {
        platform.getScheduler().runAsyncTaskAtFixedRate((o) -> {
            ConcurrentMap<User, Cache<Integer, CachedEntity>> cacheMap = cache.asMap();

            int underlyingSize = cacheMap.values().stream().mapToInt(innerCache -> (int) innerCache.estimatedSize()).sum();
            double avgCacheSizePerUser = cacheMap.isEmpty() ? 0 : (double) underlyingSize / cacheMap.size();

            long evictionCount = cache.stats().evictionCount();

            Component statsComponent = Component.text()
                    .append(Component.text("[DEBUG] Cache Stats", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .appendNewline()
                    .append(Component.text("\n\u25cf User Cache Size: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(cache.estimatedSize(), NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Average Cache Size Per User: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(avgCacheSizePerUser, NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Evicted Items: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(evictionCount, NamedTextColor.AQUA))
                    .append(Component.text("\n\u25cf Underlying Cache Size: ", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text(underlyingSize, NamedTextColor.AQUA))
                    .build();

            platform.broadcastComponent(statsComponent, "AntiHealthIndicator.Debug");
        }, 10, 10, TimeUnit.SECONDS);
    }
}