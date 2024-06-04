package com.deathmotion.antihealthindicator.util;

import com.deathmotion.antihealthindicator.AHIPlatform;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

public class ComponentCreator {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-ORX]|\\\u25cf");
    private static String pluginVersion;

    private static String getPluginVersion() {
        if (pluginVersion == null) {
            pluginVersion = AHIPlatform.class.getPackage().getImplementationVersion();
        }
        return pluginVersion;
    }

    public static Component createAHICommandComponent() {
        return Component.text()
                .append(Component.text("\u25cf", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" Running ", NamedTextColor.GRAY))
                .append(Component.text("AntiHealthIndicator", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" v", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(getPluginVersion(), NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(" by ", NamedTextColor.GRAY))
                .append(Component.text("Bram", NamedTextColor.GREEN))
                .hoverEvent(HoverEvent.showText(Component.text("Open Github Page!", NamedTextColor.GREEN)
                        .decorate(TextDecoration.UNDERLINED)))
                .clickEvent(ClickEvent.openUrl("https://github.com/Bram1903/AntiHealthIndicator"))
                .build();
    }

    public static String createLegacyAHICommandComponent() {
        return STRIP_COLOR_PATTERN
                .matcher(LegacyComponentSerializer.legacyAmpersand().serialize(ComponentCreator.createAHICommandComponent()))
                .replaceAll("")
                .trim();
    }
}
