package com.deathmotion.antihealthindicator.cache.trackers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.cache.EntityCache;
import com.deathmotion.antihealthindicator.cache.entities.PlayerEntity;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;

import java.util.List;

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
        if (event.getPacketType() != PacketType.Play.Server.PLAYER_INFO) return;

        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event);
        WrapperPlayServerPlayerInfo.Action action = packet.getAction();
        if (action == null) return;

        List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList = packet.getPlayerDataList();

        switch (action) {
            case ADD_PLAYER:
                handleAddPlayer(playerDataList);
                break;
            case REMOVE_PLAYER:
                handleRemovePlayer(playerDataList);
                break;
            case UPDATE_GAME_MODE:
                handleUpdateGamemode(playerDataList);
                break;
            case UPDATE_LATENCY:
                handleUpdateLatency(playerDataList);
                break;
            case UPDATE_DISPLAY_NAME:
                handleUpdateDisplayName(playerDataList);
                break;
        }
    }

    private void handleAddPlayer(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {
        for (WrapperPlayServerPlayerInfo.PlayerData playerData : playerDataList) {
            // How can I get the entity id of this player data object?
            PlayerEntity playerEntity = entityCache.getCachedPlayer(-1);
            if (playerEntity == null) continue;

            // TODO: Implement player entity handling
        }
    }

    private void handleRemovePlayer(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {

    }

    private void handleUpdatePlayer(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {

    }

    private void handleUpdateGamemode(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {

    }

    private void handleUpdateLatency(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {

    }

    private void handleUpdateDisplayName(List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList) {

    }
}
