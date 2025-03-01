/*
 *  This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  Copyright (C) 2025 Bram and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.cache;

import com.deathmotion.antihealthindicator.cache.entities.CachedEntity;
import com.deathmotion.antihealthindicator.cache.entities.PlayerEntity;
import com.deathmotion.antihealthindicator.cache.entities.RidableEntity;
import com.deathmotion.antihealthindicator.cache.trackers.EntityTracker;
import com.deathmotion.antihealthindicator.cache.trackers.PlayerTracker;
import com.deathmotion.antihealthindicator.cache.trackers.VehicleTracker;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EntityCache {
    private final AHIPlayer player;
    private final EntityTracker entityTracker;
    private final PlayerTracker playerTracker;
    private final VehicleTracker vehicleTracker;

    private final ConcurrentHashMap<Integer, CachedEntity> cache;
    private final ConcurrentHashMap<UUID, Integer> playerIndex;
    private final ConcurrentHashMap<Integer, Integer> passengerIndex;

    public EntityCache(AHIPlayer player) {
        this.player = player;
        this.entityTracker = new EntityTracker(player, this);
        this.playerTracker = new PlayerTracker(player, this);
        this.vehicleTracker = new VehicleTracker(player, this);

        this.cache = new ConcurrentHashMap<>();
        this.playerIndex = new ConcurrentHashMap<>();
        this.passengerIndex = new ConcurrentHashMap<>();
    }

    public void onPacketSend(PacketSendEvent event) {
        entityTracker.onPacketSend(event);
        playerTracker.onPacketSend(event);
        vehicleTracker.onPacketSend(event);
    }

    public Optional<CachedEntity> getCachedEntity(int entityId) {
        return Optional.ofNullable(cache.get(entityId));
    }

    public @Nullable PlayerEntity getCachedPlayer(int entityId) {
        CachedEntity entity = cache.get(entityId);
        if (entity instanceof PlayerEntity) {
            return (PlayerEntity) entity;
        }
        return null;
    }

    public Optional<RidableEntity> getVehicleData(int entityId) {
        CachedEntity entity = cache.get(entityId);
        if (entity instanceof RidableEntity) {
            return Optional.of((RidableEntity) entity);
        }
        return Optional.empty();
    }

    public void addLivingEntity(int entityId, @NonNull CachedEntity cachedEntity) {
        cache.put(entityId, cachedEntity);

        if (cachedEntity instanceof RidableEntity) {
            RidableEntity ridable = (RidableEntity) cachedEntity;
            // Populate the secondary index based on its current passenger ID.
            passengerIndex.put(ridable.getPassengerId(), entityId);
        }
    }

    public void addPlayer(UUID uuid, int entityId) {
        playerIndex.put(uuid, entityId);
    }

    public void removeEntity(int entityId) {
        CachedEntity removed = cache.remove(entityId);

        if (removed instanceof PlayerEntity) {
            playerIndex.values().removeIf(id -> id == entityId);
        }

        if (removed instanceof RidableEntity) {
            RidableEntity ridable = (RidableEntity) removed;
            // Remove from secondary index.
            passengerIndex.remove(ridable.getPassengerId());
        }
    }

    public void resetUserCache() {
        cache.clear();
        playerIndex.clear();
        passengerIndex.clear();
    }

    public void updateVehiclePassenger(int entityId, int newPassengerId) {
        getVehicleData(entityId).ifPresent(ridableEntity -> {
            int oldPassengerId = ridableEntity.getPassengerId();
            if (oldPassengerId != newPassengerId) {
                passengerIndex.remove(oldPassengerId);
                ridableEntity.setPassengerId(newPassengerId);
                passengerIndex.put(newPassengerId, entityId);
            }
        });
    }

    public float getVehicleHealth(int entityId) {
        return getVehicleData(entityId).map(RidableEntity::getHealth).orElse(0.5f);
    }

    public boolean isUserPassenger(int entityId) {
        return getVehicleData(entityId)
                .map(ridableEntity -> ridableEntity.getPassengerId() == player.user.getEntityId())
                .orElse(false);
    }

    public int getPassengerId(int entityId) {
        return getVehicleData(entityId).map(RidableEntity::getPassengerId).orElse(0);
    }

    public int getEntityIdByPassengerId(int passengerId) {
        return passengerIndex.getOrDefault(passengerId, 0);
    }
}
