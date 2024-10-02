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
import com.deathmotion.antihealthindicator.schedulers.BukkitScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class AHIBukkit extends JavaPlugin {
    private final BukkitAntiHealthIndicator ahi = new BukkitAntiHealthIndicator(this);

    public BukkitAntiHealthIndicator getAhi() {
        return this.ahi;
    }

    @Override
    public void onEnable() {
        ahi.commonOnInitialize();

        ahi.setScheduler(new BukkitScheduler(this));

        ahi.commonOnEnable();
        new BukkitAHICommand(this);
        ahi.enableBStats();
    }

    @Override
    public void onDisable() {
        ahi.commonOnDisable();
    }
}