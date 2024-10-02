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

package com.deathmotion.antihealthindicator.listener;

import better.reload.api.ReloadEvent;
import com.deathmotion.antihealthindicator.AHIBukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReloadListener implements Listener {
    private final AHIBukkit plugin;

    public ReloadListener(AHIBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onReloadEvent(ReloadEvent event) {
        plugin.getAhi().getConfigManager().reloadConfig();

        Component message = Component.text()
                .append(Component.text("\u25cf", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text("The configuration has been reloaded!", NamedTextColor.GREEN))
                .build();

        event.getCommandSender().sendMessage(message);
    }
}
