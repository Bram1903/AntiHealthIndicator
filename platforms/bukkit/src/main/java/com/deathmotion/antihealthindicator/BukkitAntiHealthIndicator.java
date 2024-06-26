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

import com.deathmotion.antihealthindicator.commands.BukkitAHICommand;
import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import com.deathmotion.antihealthindicator.managers.LogManager;
import io.github.retrooper.packetevents.bstats.Metrics;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@Getter
public class BukkitAntiHealthIndicator extends AHIPlatform<JavaPlugin> {

    private final JavaPlugin plugin;

    public BukkitAntiHealthIndicator(JavaPlugin plugin) {
        this.plugin = plugin;
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

    protected void setLogManager(LogManager<JavaPlugin> logManager) {
        this.logManager = logManager;
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        CommandSender commandSender = Bukkit.getPlayer(sender);
        if (commandSender == null) return false;

        return commandSender.hasPermission(permission);
    }

    @Override
    public String getPluginDirectory() {
        return this.plugin.getDataFolder().getAbsolutePath();
    }

    protected void enableBStats() {
        try {
            Metrics metrics = new Metrics(this.plugin, 20803);
            metrics.addCustomChart(new Metrics.SimplePie("antihealthindicator_version", () -> AHIPlatform.class.getPackage().getImplementationVersion()));
            metrics.addCustomChart(new Metrics.SimplePie("antihealthindicator_platform", () -> "Bukkit"));
        } catch (Exception e) {
            this.plugin.getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }

    protected void registerCommands() {
        new BukkitAHICommand(this.plugin);
    }
}