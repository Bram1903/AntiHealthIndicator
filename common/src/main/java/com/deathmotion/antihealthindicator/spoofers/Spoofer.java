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

package com.deathmotion.antihealthindicator.spoofers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.event.PacketSendEvent;

public abstract class Spoofer {

    protected final AHIPlayer player;
    protected final ConfigManager<?> configManager;

    public Spoofer(AHIPlayer player) {
        this.player = player;
        this.configManager = AHIPlatform.getInstance().getConfigManager();
    }

    public abstract void onPacketSend(final PacketSendEvent event);
}
