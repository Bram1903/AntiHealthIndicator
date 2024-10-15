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

package com.deathmotion.antihealthindicator.commands.antihealthindicator;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.commands.SubCommand;
import com.deathmotion.antihealthindicator.data.CommonUser;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Collections;
import java.util.List;

public class ReloadCommand<P> implements SubCommand<P> {
    private final ConfigManager<P> configManager;

    public ReloadCommand(AHIPlatform<P> platform) {
        this.configManager = platform.getConfigManager();
    }

    @Override
    public void execute(CommonUser<P> sender, String[] args) {
        configManager.reloadConfig();

        Component message = Component.text()
                .append(Component.text("\u25cf", NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                .append(Component.text(" The configuration has been reloaded!", NamedTextColor.GREEN))
                .build();

        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommonUser<P> sender, String[] args) {
        return Collections.emptyList();
    }
}
