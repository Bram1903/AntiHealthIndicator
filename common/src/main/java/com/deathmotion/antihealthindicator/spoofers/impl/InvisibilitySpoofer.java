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

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.cache.entities.PlayerDataStore;
import com.deathmotion.antihealthindicator.cache.entities.PlayerEntity;
import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.deathmotion.antihealthindicator.spoofers.type.GenericSpoofer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InvisibilitySpoofer extends Spoofer implements GenericSpoofer {

    private final boolean useModernInfoPacket;
    private final boolean useModernSpawnPacket;
    private final ConcurrentHashMap<UUID, UUID> UUIDMap;

    public InvisibilitySpoofer(AHIPlayer player) {
        super(player);
        this.useModernInfoPacket = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19_3);
        this.useModernSpawnPacket = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_20_2);
        this.UUIDMap = new ConcurrentHashMap<>();
    }

    public void setInvisible(PlayerEntity playerEntity) {
        if (playerEntity.getEntityId() == player.user.getEntityId()) return;

        AHIPlatform.getInstance().getScheduler().runAsyncTask((o) -> {
            PlayerDataStore playerDataStore = getPlayerDataStore(playerEntity.getEntityId());
            if (playerDataStore == null) return;

            if (useModernInfoPacket) {
                setInvisibleModern(playerEntity, playerDataStore);
            } else {
                setInvisibleLegacy(playerEntity, playerDataStore);
            }
        });
    }

    public void setVisible(PlayerEntity playerEntity) {
        AHIPlatform.getInstance().getScheduler().runAsyncTask((o) -> {
            PlayerDataStore playerDataStore = getPlayerDataStore(playerEntity.getEntityId());
            if (playerDataStore == null) return;

            if (useModernInfoPacket) {
                setVisibleModern(playerEntity, playerDataStore);
            } else {
                setVisibleLegacy(playerEntity, playerDataStore);
            }
        });
    }

    private PlayerDataStore getPlayerDataStore(int entityId) {
        UUID uuid = player.entityCache.getPlayerUUID(entityId);
        if (uuid == null) return null;

        return player.entityCache.getPlayerTracker().getPlayerDataStore(uuid);
    }

    private void setInvisibleModern(PlayerEntity playerEntity, PlayerDataStore playerDataStore) {
        UUID realUUID = playerDataStore.getProfile().getUUID();
        UUID randomUUID = UUID.randomUUID();
        UUIDMap.put(realUUID, randomUUID);

        WrapperPlayServerDestroyEntities destroyRealPlayerPacket = new WrapperPlayServerDestroyEntities(playerEntity.getEntityId());
        player.user.sendPacketSilently(destroyRealPlayerPacket);

        WrapperPlayServerPlayerInfoRemove removePacket = new WrapperPlayServerPlayerInfoRemove(realUUID);
        player.user.sendPacketSilently(removePacket);

        List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> playerInfoList = new ArrayList<>();
        EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER);
        playerInfoList.add(new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(createSpoofedProfile(randomUUID), true, -1, GameMode.SURVIVAL, Component.text("Invisible"), playerDataStore.getChatSession()));

        WrapperPlayServerPlayerInfoUpdate spawnFakePlayerPacket = new WrapperPlayServerPlayerInfoUpdate(actions, playerInfoList);
        player.user.sendPacketSilently(spawnFakePlayerPacket);

        spawnPlayer(playerEntity, randomUUID);
    }

    private void setVisibleModern(PlayerEntity playerEntity, PlayerDataStore playerDataStore) {
        UUID realUUID = playerDataStore.getProfile().getUUID();
        UUID fakeUUID = UUIDMap.remove(realUUID);
        if (fakeUUID == null) return;

        WrapperPlayServerDestroyEntities destroyFakePlayerPacket = new WrapperPlayServerDestroyEntities(playerEntity.getEntityId());
        player.user.sendPacketSilently(destroyFakePlayerPacket);

        WrapperPlayServerPlayerInfoRemove removePlayerInfoPacket = new WrapperPlayServerPlayerInfoRemove(fakeUUID);
        player.user.sendPacketSilently(removePlayerInfoPacket);

        List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> playerInfoList = new ArrayList<>();
        EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER);
        playerInfoList.add(new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(playerDataStore.getProfile(), playerDataStore.isListed(), playerDataStore.getLatency(), playerDataStore.getGameMode(), playerDataStore.getDisplayName(), playerDataStore.getChatSession()));

        WrapperPlayServerPlayerInfoUpdate spawnRealPlayerPacket = new WrapperPlayServerPlayerInfoUpdate(actions, playerInfoList);
        player.user.sendPacketSilently(spawnRealPlayerPacket);

        spawnPlayer(playerEntity, realUUID);
    }

    private void setInvisibleLegacy(PlayerEntity playerEntity, PlayerDataStore playerDataStore) {
        // TODO
    }

    private void setVisibleLegacy(PlayerEntity playerEntity, PlayerDataStore playerDataStore) {
        // TODO
    }

    private void spawnPlayer(PlayerEntity playerEntity, UUID uuid) {
        if (useModernSpawnPacket) {
            WrapperPlayServerSpawnEntity spawnPacket = new WrapperPlayServerSpawnEntity(playerEntity.getEntityId(), uuid, EntityTypes.PLAYER, playerEntity.getLocation(), 0, 0, null);
            player.user.sendPacketSilently(spawnPacket);
        }

        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata(playerEntity.getEntityId(), playerEntity.getEntityDataList());
        // This needs to be non silent so the metadata can be spoofed
        player.user.sendPacket(metadataPacket);
    }

    private UserProfile createSpoofedProfile(UUID randomUUID) {
        return new UserProfile(randomUUID, "§kInvisible");
    }
}
