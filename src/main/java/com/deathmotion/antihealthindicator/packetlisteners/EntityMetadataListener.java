package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.packetlisteners.abstracts.EntityListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.entity.Player;

public class EntityMetadataListener extends EntityListenerAbstract {

    public EntityMetadataListener(AntiHealthIndicator plugin) {
        super(plugin);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.ENTITY_METADATA)) {
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
            Player player = (Player) event.getPlayer();

            handlePacket(player, packet.getEntityId(), packet.getEntityMetadata());
            event.markForReEncode(true);
        }
    }
}