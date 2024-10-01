package com.deathmotion.antihealthindicator.packetlisteners.spoofers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardListener<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;

    // Use ConcurrentHashMap's KeySet for a thread-safe Set
    private final Set<String> healthObjectives = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a new EntityMetadataListener with the specified {@link AHIPlatform}.
     *
     * @param platform The platform to use.
     */
    public ScoreboardListener(AHIPlatform<P> platform) {
        this.platform = platform;

        platform.getLogManager().debug("Update Objective Listener initialized.");
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)) {
            WrapperPlayServerScoreboardObjective packet = new WrapperPlayServerScoreboardObjective(event);
            WrapperPlayServerScoreboardObjective.RenderType renderType = packet.getRenderType();

            if (renderType == WrapperPlayServerScoreboardObjective.RenderType.HEARTS) {
                healthObjectives.add(packet.getName());
            }
        }

        if (event.getPacketType().equals(PacketType.Play.Server.UPDATE_SCORE)) {
            WrapperPlayServerUpdateScore packet = new WrapperPlayServerUpdateScore(event);

            // Check if the value is present and then compare
            if (healthObjectives.contains(packet.getObjectiveName()) && packet.getValue().isPresent()) {
                packet.setValue(Optional.of(-1));
                event.markForReEncode(true);
            }
        }
    }
}