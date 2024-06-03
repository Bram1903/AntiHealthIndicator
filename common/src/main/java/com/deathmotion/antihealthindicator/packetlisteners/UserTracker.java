/*
 *
 *  *
 *  *  * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  *  * Copyright (C) 2024 Bram and contributors
 *  *  *
 *  *  * This program is free software: you can redistribute it and/or modify
 *  *  * it under the terms of the GNU General Public License as published by
 *  *  * the Free Software Foundation, either version 3 of the License, or
 *  *  * (at your option) any later version.
 *  *  *
 *  *  * This program is distributed in the hope that it will be useful,
 *  *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  * GNU General Public License for more details.
 *  *  *
 *  *  * You should have received a copy of the GNU General Public License
 *  *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  *
 *
 */

package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.UserLoginEvent;

public class UserTracker<P> implements PacketListener {
    private final CacheManager<P> cacheManager;

    public UserTracker(AHIPlatform<P> platform) {
        this.cacheManager = platform.getCacheManager();

        platform.getLogManager().debug("User Tracker has been set up.");
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        this.cacheManager.addUser(event.getUser());
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        this.cacheManager.removeUser(event.getUser());
    }
}
