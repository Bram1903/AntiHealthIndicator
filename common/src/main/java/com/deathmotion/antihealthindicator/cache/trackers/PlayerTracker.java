package com.deathmotion.antihealthindicator.cache.trackers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.cache.EntityCache;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class PlayerTracker {
    private final AHIPlayer player;
    private final EntityCache entityCache;
    private final ConfigManager<?> configManager;

    public PlayerTracker(AHIPlayer player, EntityCache entityCache) {
        this.player = player;
        this.entityCache = entityCache;
        this.configManager = AHIPlatform.getInstance().getConfigManager();
    }

    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
            handlePlayerInfo(new WrapperPlayServerPlayerInfo(event));
        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            handlePlayerInfoUpdate(new WrapperPlayServerPlayerInfoUpdate(event));
        } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            removePlayer(new WrapperPlayServerPlayerInfoRemove(event).getProfileIds());
        }
    }

    private void handlePlayerInfo(WrapperPlayServerPlayerInfo packet) {
        WrapperPlayServerPlayerInfo.Action action = packet.getAction();
        if (action == null) return;

        switch (action) {
            case ADD_PLAYER:
                addPlayerInfo(packet.getPlayerDataList());
                break;
            case REMOVE_PLAYER:
                removePlayerInfoLegacy(packet.getPlayerDataList());
                break;
        }
    }

    private void handlePlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate packet) {
        EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = packet.getActions();

        for (WrapperPlayServerPlayerInfoUpdate.Action action : actions) {
            switch (action) {
                case ADD_PLAYER:
                    addPlayerInfoUpdate(packet.getEntries());
                    break;
            }
        }
    }

    private void addPlayerInfo(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {

    }

    private void addPlayerInfoUpdate(List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> playerInfoList) {

    }

    private void removePlayerInfoLegacy(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {

    }

    private void removePlayer(List<UUID> profileUUIDs) {
    }
}
