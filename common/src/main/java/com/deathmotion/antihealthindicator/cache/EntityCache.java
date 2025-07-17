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
import com.deathmotion.antihealthindicator.cache.entities.RidableEntity;
import com.deathmotion.antihealthindicator.cache.trackers.EntityTracker;
import com.deathmotion.antihealthindicator.cache.trackers.VehicleTracker;
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EntityCache {
    private final AHIPlayer player;
    private final EntityTracker entityTracker;
    private final VehicleTracker vehicleTracker;

    private final ConcurrentHashMap<Integer, CachedEntity> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Integer> passengerIndex = new ConcurrentHashMap<>();

    public EntityCache(AHIPlayer player) {
        this.player = player;
        this.entityTracker = new EntityTracker(player, this);
        this.vehicleTracker = new VehicleTracker(player, this);
    }

    public void onPacketSend(PacketSendEvent event) {
        entityTracker.onPacketSend(event);
        vehicleTracker.onPacketSend(event);
    }

    public boolean isRideableVehicle(int entityId) {
        CachedEntity ce = cache.get(entityId);
        return ce instanceof RidableEntity;
    }

    public void addLivingEntity(int entityId, CachedEntity entity) {
        cache.put(entityId, entity);
        if (entity instanceof RidableEntity) {
            RidableEntity r = (RidableEntity) entity;
            passengerIndex.put(r.getPassengerId(), entityId);
        }
    }

    public void removeEntity(int entityId) {
        CachedEntity removed = cache.remove(entityId);
        if (removed instanceof RidableEntity) {
            passengerIndex.remove(((RidableEntity) removed).getPassengerId());
        }
    }

    public void resetUserCache() {
        cache.clear();
        passengerIndex.clear();
    }

    public void updateVehiclePassenger(int vehicleId, int newPassengerId) {
        cache.computeIfPresent(vehicleId, (vid, ce) -> {
            if (ce instanceof RidableEntity) {
                RidableEntity r = (RidableEntity) ce;
                int oldPid = r.getPassengerId();
                if (oldPid != newPassengerId) {
                    passengerIndex.remove(oldPid);
                    r.setPassengerId(newPassengerId);
                    passengerIndex.put(newPassengerId, vehicleId);
                }
            }
            return ce;
        });
    }

    public CachedEntity getEntityRaw(int entityId) {
        return cache.get(entityId);
    }

    public float getVehicleHealth(int vehicleId) {
        CachedEntity ce = cache.get(vehicleId);
        if (ce instanceof RidableEntity) {
            return ((RidableEntity) ce).getHealth();
        }
        return 0.5F;
    }

    public int getPassengerId(int vehicleId) {
        CachedEntity ce = cache.get(vehicleId);
        if (ce instanceof RidableEntity) {
            return ((RidableEntity) ce).getPassengerId();
        }
        return 0;
    }

    public boolean isUserPassenger(int entityId) {
        return getPassengerId(entityId) == player.user.getEntityId();
    }

    public int getEntityIdByPassengerId(int passengerId) {
        return passengerIndex.getOrDefault(passengerId, 0);
    }
}
