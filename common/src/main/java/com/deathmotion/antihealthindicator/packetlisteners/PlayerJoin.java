package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.concurrent.TimeUnit;

public class PlayerJoin<P> extends PacketListenerAbstract {
    private final AHIPlatform<P> platform;
    private final Component updateComponent;

    public PlayerJoin(AHIPlatform<P> platform, String latestVersion) {
        this.platform = platform;

        this.updateComponent = Component.text()
                .append(Component.text("[AntiHealthIndicator] ", NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text("Version " + latestVersion + " is ", NamedTextColor.GREEN))
                .append(Component.text("now available", NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to download", NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.openUrl("https://www.spigotmc.org/resources/antihealthindicator.20803/")))
                .append(Component.text("!", NamedTextColor.GREEN))
                .build();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (PacketType.Play.Server.JOIN_GAME == event.getPacketType()) {
            User user = event.getUser();

            platform.getScheduler().rynAsyncTaskDelayed((o) -> {
                if (platform.hasPermission(user.getUUID(), "AntiHealthIndicator.Notify")) {
                    user.sendMessage(updateComponent);
                }
            }, 2, TimeUnit.SECONDS);
        }
    }
}