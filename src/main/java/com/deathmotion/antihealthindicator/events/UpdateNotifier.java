package com.deathmotion.antihealthindicator.events;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import io.github.retrooper.packetevents.util.FoliaCompatUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotifier implements Listener {

    private final AntiHealthIndicator plugin;

    private final BukkitAudiences adventure;

    private final Component component;

    public UpdateNotifier(AntiHealthIndicator plugin, String latestVersion) {
        this.plugin = plugin;
        this.adventure = plugin.getAdventure();

        String downloadLink = "https://www.spigotmc.org/resources/antihealthindicator.114851//";
        this.component = Component.text()
                .append(Component.text("[AntiHealthIndicator] ", NamedTextColor.RED))
                .append(Component.text("Version " + latestVersion + " is ", NamedTextColor.GREEN))
                .append(Component.text("now available", NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to download", NamedTextColor.GREEN)))
                        .clickEvent(ClickEvent.openUrl(downloadLink)))
                .append(Component.text("! ", NamedTextColor.GREEN))
                .build();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("AntiHealthIndicator.Notify")) {
            FoliaCompatUtil.runTaskTimerAsync(this.plugin, object -> {
                adventure.player(player).sendMessage(component);
            }, 40L, 20L * 60 * 60);
        }
    }
}