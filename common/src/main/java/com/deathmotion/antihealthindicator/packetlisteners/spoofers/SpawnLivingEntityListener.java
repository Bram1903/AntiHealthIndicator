package com.deathmotion.antihealthindicator.packetlisteners.spoofers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.packetlisteners.spoofers.abstracts.EntityListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;

public class SpawnLivingEntityListener<P> extends EntityListenerAbstract<P> {

    public SpawnLivingEntityListener(AHIPlatform<P> platform) {
        super(platform);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.SPAWN_LIVING_ENTITY)) {
            WrapperPlayServerSpawnLivingEntity packet = new WrapperPlayServerSpawnLivingEntity(event);

            handlePacket(event.getUser(), packet.getEntityId(), packet.getEntityMetadata());
            event.markForReEncode(true);
        }
    }
}