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
import com.deathmotion.antihealthindicator.cache.trackers.EntityTracker;
import com.deathmotion.antihealthindicator.cache.trackers.VehicleTracker;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.RidableEntity;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EntityCache {
    private final AHIPlayer player;
    private final ConcurrentHashMap<Integer, CachedEntity> cache;
    private final EntityTracker entityTracker;
    private final VehicleTracker vehicleTracker;

    public EntityCache(AHIPlayer player) {
        this.player = player;
        this.cache = new ConcurrentHashMap<>();
        this.entityTracker = new EntityTracker(player, this);
        this.vehicleTracker = new VehicleTracker(player, this);
    }

    public void onPacketSend(PacketSendEvent event) {
        entityTracker.onPacketSend(event);
        vehicleTracker.onPacketSend(event);
    }

    public Optional<CachedEntity> getCachedEntity(int entityId) {
        return Optional.ofNullable(cache.get(entityId));
    }

    public Optional<RidableEntity> getVehicleData(int entityId) {
        return getCachedEntity(entityId)
                .filter(entityData -> entityData instanceof RidableEntity)
                .map(entityData -> (RidableEntity) entityData);
    }

    public void addLivingEntity(int entityId, @NonNull CachedEntity cachedEntity) {
        cache.put(entityId, cachedEntity);
    }

    public void removeEntity(int entityId) {
        cache.remove(entityId);
    }

    public void resetUserCache() {
        cache.clear();
    }

    public void updateVehiclePassenger(int entityId, int passengerId) {
        getVehicleData(entityId).ifPresent(ridableEntityData -> ridableEntityData.setPassengerId(passengerId));
    }

    public float getVehicleHealth(int entityId) {
        return getVehicleData(entityId).map(RidableEntity::getHealth).orElse(0.5f);
    }

    public boolean isUserPassenger(int entityId) {
        return getVehicleData(entityId).map(ridableEntityData -> ridableEntityData.getPassengerId() == player.user.getEntityId()).orElse(false);
    }

    public int getPassengerId(int entityId) {
        return getVehicleData(entityId).map(RidableEntity::getPassengerId).orElse(0);
    }

    public int getEntityIdByPassengerId(int passengerId) {
        return cache.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof RidableEntity && ((RidableEntity) entry.getValue()).getPassengerId() == passengerId)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);
    }
}