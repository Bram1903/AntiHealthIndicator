/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2025 Bram and contributors
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

package com.deathmotion.antihealthindicator.testplugin;

import com.deathmotion.antihealthindicator.api.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.api.AntiHealthIndicatorAPI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class ApiTestPlugin extends JavaPlugin {

    private AntiHealthIndicatorAPI api;

    @Override
    public void onEnable() {
        if (!(getServer().getPluginManager().isPluginEnabled("AntiHealthIndicator"))) {
            getLogger().severe("AntiHealthIndicator is not enabled! This plugin will be disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        api = AntiHealthIndicator.getAPI();
        getLogger().info("Successfully hooked into the AntiHealthIndicator API running version " + api.getVersion().toStringWithoutSnapshot());
    }

    @Override
    public void onDisable() {
    }
}
