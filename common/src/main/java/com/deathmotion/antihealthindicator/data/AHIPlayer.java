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

package com.deathmotion.antihealthindicator.data;

import com.deathmotion.antihealthindicator.cache.EntityCache;
import com.deathmotion.antihealthindicator.managers.SpoofManager;
import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.protocol.player.User;

import java.util.UUID;

public class AHIPlayer {
    public final UUID uuid;
    public final User user;

    public final MetadataIndex metadataIndex;
    public final EntityCache entityCache;

    public final SpoofManager spoofManager;

    public AHIPlayer(User user) {
        this.uuid = user.getUUID();
        this.user = user;

        this.metadataIndex = new MetadataIndex(user.getClientVersion());
        this.entityCache = new EntityCache(this);

        this.spoofManager = new SpoofManager(this);
    }
}
