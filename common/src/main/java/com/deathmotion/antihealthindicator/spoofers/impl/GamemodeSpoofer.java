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

package com.deathmotion.antihealthindicator.spoofers.impl;

import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.deathmotion.antihealthindicator.models.Settings;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public final class GamemodeSpoofer extends Spoofer {

    public GamemodeSpoofer(AHIPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        Settings settings = configManager.getSettings();
        if (!settings.isGamemode()) return;

        final PacketTypeCommon packetType = event.getPacketType();

        if (packetType == PacketType.Play.Server.PLAYER_INFO) {
            handlePlayerInfo(event, new WrapperPlayServerPlayerInfo(event));
        } else if (packetType == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            handlePlayerInfoUpdate(event, new WrapperPlayServerPlayerInfoUpdate(event));
        }
    }

    private void handlePlayerInfo(PacketSendEvent event, WrapperPlayServerPlayerInfo packet) {
        WrapperPlayServerPlayerInfo.Action action = packet.getAction();
        if (action == null) return;

        switch (action) {
            case ADD_PLAYER:
            case UPDATE_GAME_MODE:
                spoofLegacyGameModes(packet.getPlayerDataList());
                event.markForReEncode(true);
                break;
            default:
                break;
        }
    }

    private void handlePlayerInfoUpdate(PacketSendEvent event, WrapperPlayServerPlayerInfoUpdate packet) {
        EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = packet.getActions();
        for (WrapperPlayServerPlayerInfoUpdate.Action action : actions) {
            switch (action) {
                case ADD_PLAYER:
                case UPDATE_GAME_MODE:
                    spoofModernGameModes(packet.getEntries());
                    event.markForReEncode(true);
                    break;
                default:
                    break;
            }
        }
    }

    private void spoofLegacyGameModes(List<WrapperPlayServerPlayerInfo.PlayerData> list) {
        for (WrapperPlayServerPlayerInfo.PlayerData data : list) {
            UUID uuid = data.getUserProfile().getUUID();
            if (uuid.equals(player.uuid)) continue;
            if (data.getGameMode() == GameMode.SPECTATOR) continue;
            data.setGameMode(GameMode.SURVIVAL);
        }
    }

    private void spoofModernGameModes(List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> list) {
        for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo info : list) {
            UUID uuid = info.getGameProfile().getUUID();
            if (uuid.equals(player.uuid)) continue;
            if (info.getGameMode() == GameMode.SPECTATOR) continue;
            info.setGameMode(GameMode.SURVIVAL);
        }
    }
}
