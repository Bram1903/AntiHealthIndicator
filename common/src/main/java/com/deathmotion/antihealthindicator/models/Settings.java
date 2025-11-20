/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2025 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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

package com.deathmotion.antihealthindicator.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {
    private boolean debug = false;

    private UpdateChecker updateChecker = new UpdateChecker();
    private boolean foodSaturation = false;
    private boolean gamemode = true;
    private boolean teamScoreboard = true;
    private EntityData entityData = new EntityData();
    private Items items = new Items();

    @Getter
    @Setter
    public static class UpdateChecker {
        private boolean enabled = true;
        private boolean printToConsole = true;
        private boolean notifyInGame = true;
    }

    @Getter
    @Setter
    public static class EntityData {
        private boolean enabled = true;
        private boolean playersOnly = false;

        private boolean airTicks = true;
        private boolean health = true;
        private boolean absorption = true;
        private boolean xp = true;
    }

    @Getter
    @Setter
    public static class Items {
        private boolean enabled = true;
        private boolean stackAmount = true;

        private boolean durability = true;
        private boolean brokenElytra = true;

        private boolean enchantments = true;
    }
}
