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

package com.deathmotion.antihealthindicator.packets;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;

public class SpoofManagerPacketListener<P> extends PacketListenerAbstract {

    private final AHIPlatform<P> platform;

    public SpoofManagerPacketListener(AHIPlatform<P> platform) {
        super(PacketListenerPriority.LOW);
        this.platform = platform;
    }

    @Override
    public void onPacketSend(final PacketSendEvent event) {
        if (event.getConnectionState() != ConnectionState.PLAY) return;
        AHIPlayer player = platform.getPlayerDataManager().getPlayer(event.getUser());
        if (player == null) return;

        // PacketEvents re-encodes every packet a listener wrapped, unless told otherwise, and encoding an item
        // is expensive. So the flag is cleared here and the spoofers set it again only if they changed something.
        // A wrapper that already exists means an earlier listener read the packet and may have edited it.
        final PacketTypeCommon type = event.getPacketType();
        final boolean flagCleared = (type == PacketType.Play.Server.ENTITY_EQUIPMENT || type == PacketType.Play.Server.ENTITY_METADATA)
                && event.getLastUsedWrapper() == null;
        if (flagCleared) event.markForReEncode(false);

        try {
            player.entityCache.onPacketSend(event);
            player.spoofManager.onPacketSend(event);
        } catch (RuntimeException e) {
            if (flagCleared) event.markForReEncode(true);
            throw e;
        }

        if (flagCleared && !canKeepOriginalBytes(event, player)) {
            event.markForReEncode(true);
        }
    }

    /**
     * Later listeners may depend on the default re-encode. LibsDisguises edits the viewer's own metadata in place
     * without marking it, so packets about the viewer keep the default, and so do packets this plugin did not read.
     */
    private boolean canKeepOriginalBytes(PacketSendEvent event, AHIPlayer player) {
        final PacketWrapper<?> wrapper = event.getLastUsedWrapper();
        final int entityId;

        if (wrapper instanceof WrapperPlayServerEntityEquipment) {
            entityId = ((WrapperPlayServerEntityEquipment) wrapper).getEntityId();
        } else if (wrapper instanceof WrapperPlayServerEntityMetadata) {
            entityId = ((WrapperPlayServerEntityMetadata) wrapper).getEntityId();
        } else {
            return false;
        }

        return entityId != player.user.getEntityId();
    }
}
