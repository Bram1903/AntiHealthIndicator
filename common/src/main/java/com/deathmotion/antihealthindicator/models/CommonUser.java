/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2025 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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

package com.deathmotion.antihealthindicator.models;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class CommonUser<P> {
    private final AHIPlatform<P> platform;

    private final User user;
    private final boolean isConsole;

    public CommonUser(AHIPlatform<P> platform, User user) {
        this.platform = platform;

        this.user = user;
        this.isConsole = user == null;
    }

    public void sendMessage(Component message) {
        if (isConsole) {
            platform.sendConsoleMessage(message);
        } else {
            user.sendMessage(message);
        }
    }

    public boolean hasPermission(String permission) {
        return isConsole || platform.hasPermission(user.getUUID(), permission);
    }
}
