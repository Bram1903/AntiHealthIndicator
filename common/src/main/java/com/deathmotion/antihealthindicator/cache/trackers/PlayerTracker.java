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

package com.deathmotion.antihealthindicator.cache.trackers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.cache.EntityCache;
import com.deathmotion.antihealthindicator.cache.entities.PlayerDataStore;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.PlayerData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PlayerTracker {
    private final AHIPlayer player;
    private final EntityCache entityCache;
    private final ConfigManager<?> configManager;

    private final ConcurrentHashMap<UUID, PlayerDataStore> playerDataStore;

    public PlayerTracker(AHIPlayer player, EntityCache entityCache) {
        this.player = player;
        this.entityCache = entityCache;
        this.configManager = AHIPlatform.getInstance().getConfigManager();

        this.playerDataStore = new ConcurrentHashMap<>();
    }

    /**
     * Called when a packet is sent. Dispatches the packet to the appropriate handler.
     */
    public void onPacketSend(PacketSendEvent event) {
        final PacketTypeCommon packetType = event.getPacketType();

        if (packetType == PacketType.Play.Server.PLAYER_INFO) {
            handlePlayerInfo(new WrapperPlayServerPlayerInfo(event));
        } else if (packetType == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            handlePlayerInfoUpdate(new WrapperPlayServerPlayerInfoUpdate(event));
        } else if (packetType == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            removePlayers(new WrapperPlayServerPlayerInfoRemove(event).getProfileIds());
        }
    }

    private void handlePlayerInfo(WrapperPlayServerPlayerInfo packet) {
        WrapperPlayServerPlayerInfo.Action action = packet.getAction();
        if (action == null) return;

        switch (action) {
            case ADD_PLAYER:
                addPlayerInfoLegacy(packet.getPlayerDataList());
                break;
            case UPDATE_DISPLAY_NAME:
                updateDisplayNameLegacy(packet.getPlayerDataList());
                break;
            case UPDATE_GAME_MODE:
                updateGameModeLegacy(packet.getPlayerDataList());
                break;
            case UPDATE_LATENCY:
                updateLatencyLegacy(packet.getPlayerDataList());
                break;
            case REMOVE_PLAYER:
                removePlayerInfoLegacy(packet.getPlayerDataList());
                break;
        }
    }

    private void handlePlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate packet) {
        for (WrapperPlayServerPlayerInfoUpdate.Action action : packet.getActions()) {
            switch (action) {
                case ADD_PLAYER:
                    addPlayerInfo(packet.getEntries());
                    break;
                case UPDATE_DISPLAY_NAME:
                    updateDisplayName(packet.getEntries());
                    break;
                case UPDATE_GAME_MODE:
                    updateGameMode(packet.getEntries());
                    break;
                case UPDATE_LATENCY:
                    updateLatency(packet.getEntries());
                    break;
                case UPDATE_LISTED:
                    updateListed(packet.getEntries());
                    break;
            }
        }
    }

    /* ========================= Helper Methods ========================= */

    /**
     * Returns true if the given UUID represents our own player's UUID.
     */
    private boolean isOwnPlayer(UUID uuid) {
        return uuid.equals(player.uuid);
    }

    /**
     * Retrieves a player's data store from the cache and applies the given updater.
     * If the player is the current one or the data store is missing, logs a debug message.
     */
    private void updateDataStore(UUID uuid, Consumer<PlayerDataStore> updater) {
        if (isOwnPlayer(uuid)) {
            AHIPlatform.getInstance().debug("Skipping the player info update for our own player");
            return;
        }
        PlayerDataStore store = playerDataStore.get(uuid);
        if (store == null) {
            AHIPlatform.getInstance().debug("Player data store not found for " + uuid);
            return;
        }
        updater.accept(store);
    }

    /**
     * Creates and returns a new PlayerDataStore instance from legacy PlayerData.
     */
    private PlayerDataStore createDataStoreFromLegacy(PlayerData data) {
        PlayerDataStore store = new PlayerDataStore();
        store.setProfile(data.getUserProfile());
        store.setLatency(data.getPing());
        store.setGameMode(data.getGameMode());
        store.setDisplayName(data.getDisplayName());
        store.setSignatureData(data.getSignatureData());
        return store;
    }

    /**
     * Creates and returns a new PlayerDataStore instance from modern PlayerInfo.
     */
    private PlayerDataStore createDataStore(PlayerInfo info) {
        PlayerDataStore store = new PlayerDataStore();
        store.setProfile(info.getGameProfile());
        store.setListed(info.isListed());
        store.setLatency(info.getLatency());
        store.setGameMode(info.getGameMode());
        store.setDisplayName(info.getDisplayName());
        store.setChatSession(info.getChatSession());
        store.setListOrder(info.getListOrder());
        store.setShowHat(info.isShowHat());
        return store;
    }

    /* ========================= Packet Handlers ========================= */

    private void addPlayerInfoLegacy(List<PlayerData> playerDataList) {
        for (PlayerData data : playerDataList) {
            UUID uuid = data.getUserProfile().getUUID();
            if (isOwnPlayer(uuid)) {
                AHIPlatform.getInstance().debug("Skipping the player info update for our own player");
                continue;
            }
            PlayerDataStore store = createDataStoreFromLegacy(data);
            playerDataStore.put(uuid, store);
        }
    }

    private void addPlayerInfo(List<PlayerInfo> playerInfoList) {
        for (PlayerInfo info : playerInfoList) {
            UUID uuid = info.getGameProfile().getUUID();
            if (isOwnPlayer(uuid)) {
                AHIPlatform.getInstance().debug("Skipping the player info update for our own player");
                continue;
            }
            PlayerDataStore store = createDataStore(info);
            playerDataStore.put(uuid, store);
        }
    }

    private void removePlayerInfoLegacy(List<PlayerData> playerDataList) {
        for (PlayerData data : playerDataList) {
            playerDataStore.remove(data.getUserProfile().getUUID());
        }
    }

    private void removePlayers(List<UUID> profileUUIDs) {
        for (UUID uuid : profileUUIDs) {
            playerDataStore.remove(uuid);
        }
    }

    private void updateDisplayName(List<PlayerInfo> playerInfoList) {
        for (PlayerInfo info : playerInfoList) {
            UUID uuid = info.getGameProfile().getUUID();
            updateDataStore(uuid, store -> store.setDisplayName(info.getDisplayName()));
        }
    }

    private void updateDisplayNameLegacy(List<PlayerData> playerDataList) {
        for (PlayerData data : playerDataList) {
            UUID uuid = data.getUserProfile().getUUID();
            updateDataStore(uuid, store -> store.setDisplayName(data.getDisplayName()));
        }
    }

    private void updateGameMode(List<PlayerInfo> playerInfoList) {
        for (PlayerInfo info : playerInfoList) {
            UUID uuid = info.getGameProfile().getUUID();
            updateDataStore(uuid, store -> store.setGameMode(info.getGameMode()));
        }
    }

    private void updateGameModeLegacy(List<PlayerData> playerDataList) {
        for (PlayerData data : playerDataList) {
            UUID uuid = data.getUserProfile().getUUID();
            updateDataStore(uuid, store -> store.setGameMode(data.getGameMode()));
        }
    }

    private void updateLatency(List<PlayerInfo> playerInfoList) {
        for (PlayerInfo info : playerInfoList) {
            UUID uuid = info.getGameProfile().getUUID();
            updateDataStore(uuid, store -> store.setLatency(info.getLatency()));
        }
    }

    private void updateLatencyLegacy(List<PlayerData> playerDataList) {
        for (PlayerData data : playerDataList) {
            UUID uuid = data.getUserProfile().getUUID();
            updateDataStore(uuid, store -> store.setLatency(data.getPing()));
        }
    }

    private void updateListed(List<PlayerInfo> playerInfoList) {
        for (PlayerInfo info : playerInfoList) {
            UUID uuid = info.getGameProfile().getUUID();
            updateDataStore(uuid, store -> store.setListed(info.isListed()));
        }
    }
}
