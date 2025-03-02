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

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.spoofers.impl.*;
import com.deathmotion.antihealthindicator.spoofers.type.AbstractSpoofer;
import com.deathmotion.antihealthindicator.spoofers.type.GenericSpoofer;
import com.deathmotion.antihealthindicator.spoofers.type.PacketSpoofer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;

public class SpoofManager {

    public ClassToInstanceMap<AbstractSpoofer> allSpoofers;
    ClassToInstanceMap<PacketSpoofer> packetSpoofers;
    ClassToInstanceMap<GenericSpoofer> genericSpoofers;

    public SpoofManager(AHIPlayer player) {
        packetSpoofers = new ImmutableClassToInstanceMap.Builder<PacketSpoofer>()
                .put(MetadataSpoofer.class, new MetadataSpoofer(player))
                .put(EquipmentSpoofer.class, new EquipmentSpoofer(player))
                .put(ScoreboardSpoofer.class, new ScoreboardSpoofer(player))
                .put(FoodSaturationSpoofer.class, new FoodSaturationSpoofer(player))
                .put(WorldSeedSpoofer.class, new WorldSeedSpoofer(player))
                .build();

        genericSpoofers = new ImmutableClassToInstanceMap.Builder<GenericSpoofer>()
                .put(InvisibilitySpoofer.class, new InvisibilitySpoofer(player))
                .build();

        allSpoofers = new ImmutableClassToInstanceMap.Builder<AbstractSpoofer>()
                .putAll(packetSpoofers)
                .putAll(genericSpoofers)
                .build();
    }

    public void onPacketSend(final PacketSendEvent packet) {
        for (PacketSpoofer packetSpoofer : packetSpoofers.values()) {
            packetSpoofer.onPacketSend(packet);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends GenericSpoofer> T getGenericSpoofer(Class<T> check) {
        return (T) genericSpoofers.get(check);
    }
}
