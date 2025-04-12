package com.deathmotion.antihealthindicator.spoofers.impl;

import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.deathmotion.antihealthindicator.spoofers.type.PacketSpoofer;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;

import java.util.List;
import java.util.UUID;

public class GamemodeSpoofer extends Spoofer implements PacketSpoofer {

    public GamemodeSpoofer(AHIPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        final PacketTypeCommon packetType = event.getPacketType();

        if (packetType == PacketType.Play.Server.PLAYER_INFO) {
            Settings settings = configManager.getSettings();
            if (!settings.isGamemode()) return;

            handlePlayerInfo(event, new WrapperPlayServerPlayerInfo(event));
        } else if (packetType == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            Settings settings = configManager.getSettings();
            if (!settings.isGamemode()) return;

            handlePlayerInfoUpdate(event, new WrapperPlayServerPlayerInfoUpdate(event));
        }
    }

    private void handlePlayerInfo(PacketSendEvent event, WrapperPlayServerPlayerInfo packet) {
        WrapperPlayServerPlayerInfo.Action action = packet.getAction();
        if (action == null) return;

        switch (action) {
            case ADD_PLAYER:
                addPlayerInfoLegacy(packet.getPlayerDataList());
                event.markForReEncode(true);
                break;
            case UPDATE_GAME_MODE:
                updateGameModeLegacy(packet.getPlayerDataList());
                event.markForReEncode(true);
                break;
        }
    }

    private void handlePlayerInfoUpdate(PacketSendEvent event, WrapperPlayServerPlayerInfoUpdate packet) {
        for (WrapperPlayServerPlayerInfoUpdate.Action action : packet.getActions()) {
            switch (action) {
                case ADD_PLAYER:
                    addPlayerInfo(packet.getEntries());
                    event.markForReEncode(true);
                    break;
                case UPDATE_GAME_MODE:
                    updateGameMode(packet.getEntries());
                    event.markForReEncode(true);
                    break;
            }
        }
    }

    private void addPlayerInfoLegacy(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {
        for (WrapperPlayServerPlayerInfo.PlayerData data : playerDataList) {
            UUID uuid = data.getUserProfile().getUUID();
            if (uuid.equals(player.uuid)) continue;

            if (data.getGameMode() == GameMode.SPECTATOR) continue;
            data.setGameMode(GameMode.SURVIVAL);
        }
    }

    private void addPlayerInfo(List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> playerInfoList) {
        for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo info : playerInfoList) {
            UUID uuid = info.getGameProfile().getUUID();
            if (uuid.equals(player.uuid)) continue;

            if (info.getGameMode() == GameMode.SPECTATOR) continue;
            info.setGameMode(GameMode.SURVIVAL);
        }
    }

    private void updateGameMode(List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> playerInfoList) {
        for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo info : playerInfoList) {
            UUID uuid = info.getGameProfile().getUUID();
            if (uuid.equals(player.uuid)) continue;

            if (info.getGameMode() == GameMode.SPECTATOR) continue;
            info.setGameMode(GameMode.SURVIVAL);
        }
    }

    private void updateGameModeLegacy(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {
        for (WrapperPlayServerPlayerInfo.PlayerData data : playerDataList) {
            UUID uuid = data.getUserProfile().getUUID();
            if (uuid.equals(player.uuid)) continue;

            if (data.getGameMode() == GameMode.SPECTATOR) continue;
            data.setGameMode(GameMode.SURVIVAL);
        }
    }
}
