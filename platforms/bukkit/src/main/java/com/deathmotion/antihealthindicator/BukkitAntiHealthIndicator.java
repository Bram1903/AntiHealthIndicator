/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@Getter
public class BukkitAntiHealthIndicator extends AHIPlatform<JavaPlugin> {

    private final JavaPlugin plugin;
    private final boolean useAdventure;

    public BukkitAntiHealthIndicator(JavaPlugin plugin) {
        this.plugin = plugin;

        useAdventure = checkAdventureCompatibility();
    }

    private static boolean checkAdventureCompatibility() {
        try {
            Class.forName("io.papermc.paper.adventure.PaperAdventure");
            return true;
        } catch (ClassNotFoundException e) {
            // ignored exception
        }

        try {
            Class.forName("net.kyori.adventure.platform.bukkit.BukkitAudience");
            return true;
        } catch (ClassNotFoundException e) {
            // ignored exception
        }

        return false;
    }

    @Override
    public JavaPlugin getPlatform() {
        return this.plugin;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    protected void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        CommandSender commandSender = Bukkit.getPlayer(sender);
        if (commandSender == null) return false;

        return commandSender.hasPermission(permission);
    }

    @Override
    public void sendConsoleMessage(Component message) {
        if (useAdventure) {
            Bukkit.getConsoleSender().sendMessage(message);
        } else {
            Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacySection().serialize(message));
        }
    }

    @Override
    public String getPluginDirectory() {
        return this.plugin.getDataFolder().getAbsolutePath();
    }

    protected void enableBStats() {
        try {
            Metrics metrics = new Metrics(this.plugin, 20803);
            metrics.addCustomChart(new SimplePie("antihealthindicator_version", () -> AHIPlatform.class.getPackage().getImplementationVersion()));
            metrics.addCustomChart(new SimplePie("antihealthindicator_platform", () -> "Bukkit"));
        } catch (Exception e) {
            this.plugin.getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}