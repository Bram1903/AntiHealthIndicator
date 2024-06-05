package com.deathmotion.antihealthindicator.util;

import com.deathmotion.antihealthindicator.AHIBukkit;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.LogManager;
import com.github.retrooper.packetevents.util.ColorUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class BukkitLogManager extends LogManager<JavaPlugin> {
    private final String prefixText = ColorUtil.toString(NamedTextColor.AQUA) + "[AntiHealthIndicator] " + ColorUtil.toString(NamedTextColor.WHITE);
    private final boolean isDebugEnabled;

    public BukkitLogManager(AHIBukkit plugin) {
        super(plugin.getAhi());
        this.isDebugEnabled = plugin.getAhi().getConfigurationOption(ConfigOption.DEBUG_ENABLED);
    }

    @Override
    protected void log(Level level, @Nullable NamedTextColor color, String message) {
        Bukkit.getConsoleSender().sendMessage(prefixText + ColorUtil.toString(color) + message);
    }

    @Override
    public void info(String message) {
        log(Level.INFO, NamedTextColor.WHITE, message);
    }

    @Override
    public void warn(final String message) {
        log(Level.WARNING, NamedTextColor.YELLOW, message);
    }

    @Override
    public void severe(String message) {
        log(Level.SEVERE, NamedTextColor.RED, message);
    }

    @Override
    public void debug(String message) {
        if (isDebugEnabled) {
            log(Level.FINE, NamedTextColor.GRAY, message);
        }
    }
}
