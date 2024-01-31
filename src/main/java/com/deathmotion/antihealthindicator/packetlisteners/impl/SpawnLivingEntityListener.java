package com.deathmotion.antihealthindicator.packetlisteners.impl;

import com.deathmotion.antihealthindicator.packetlisteners.impl.abstracts.EntityListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnLivingEntityListener extends EntityListenerAbstract {

    public SpawnLivingEntityListener(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.SPAWN_LIVING_ENTITY)) {
            WrapperPlayServerSpawnLivingEntity packet = new WrapperPlayServerSpawnLivingEntity(event);
            Player player = (Player) event.getPlayer();

            handlePacket(player, packet.getEntityId(), packet.getEntityMetadata());
            event.markForReEncode(true);
        }
    }
}