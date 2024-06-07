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

package com.deathmotion.antihealthindicator.data;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String GITHUB_API_URL = "https://api.github.com/repos/Bram1903/AntiHealthIndicator/releases/latest";
    public static final String GITHUB_URL = "https://github.com/Bram1903/AntiHealthIndicator";
    public static final String DISCORD_URL = "https://ahi.deathmotion.com";
    public static final String SPIGOT_URL = "https://www.spigotmc.org/resources/antihealthindicator.114851/";

    public static final List<SubCommand> SUB_COMMANDS = Arrays.asList(
            new SubCommand("help", "Shows all available commands"),
            new SubCommand("discord", "Shows the Discord invite link")
    );
}

