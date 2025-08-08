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
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class EntityCache {
    private final AHIPlayer player;
    private final EntityTracker entityTracker;
    private final VehicleTracker vehicleTracker;

    private final ConcurrentHashMap<Integer, CachedEntity> cache = new ConcurrentHashMap<>();
    @Setter
    private Integer currentVehicleId = null;

    public EntityCache(AHIPlayer player) {
        this.player = player;
        this.entityTracker = new EntityTracker(player, this);
        this.vehicleTracker = new VehicleTracker(player, this);
    }

    public void onPacketSend(PacketSendEvent event) {
        entityTracker.onPacketSend(event);
        vehicleTracker.onPacketSend(event);
    }

    public void addLivingEntity(int entityId, CachedEntity entity) {
        cache.put(entityId, entity);
    }

    public void removeEntity(int entityId) {
        cache.remove(entityId);
    }

    public void resetUserCache() {
        cache.clear();
    }

    public CachedEntity getEntity(int entityId) {
        return cache.get(entityId);
    }

    public Optional<Integer> getCurrentVehicleId() {
        return Optional.ofNullable(currentVehicleId);
    }

}
