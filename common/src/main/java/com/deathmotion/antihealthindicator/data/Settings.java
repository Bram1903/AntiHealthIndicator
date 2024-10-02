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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {
    private boolean Debug = false;

    private UpdateChecker UpdateChecker = new UpdateChecker();
    private boolean AllowBypass = false;
    private boolean WorldSeed = false;
    private boolean FoodSaturation = true;
    private boolean TeamScoreboard = true;
    private EntityData EntityData = new EntityData();
    private Items Items = new Items();

    @Getter
    @Setter
    public static class UpdateChecker {
        private boolean Enabled = true;
        private boolean PrintToConsole = true;
        private boolean NotifyInGame = true;
    }

    @Getter
    @Setter
    public static class EntityData {
        private boolean Enabled = true;
        private boolean PlayersOnly = false;
        private boolean AirTicks = true;

        private boolean Health = true;
        private boolean IgnoreVehicles = true;

        private Wolves Wolves = new Wolves();
        private IronGolems IronGolems = new IronGolems();
        private boolean Absorption = true;
        private boolean Xp = true;

        @Getter
        @Setter
        public static class Wolves {
            private boolean Enabled = true;
            private boolean Tamed = false;
            private boolean Owner = true;
        }

        @Getter
        @Setter
        public static class IronGolems {
            private boolean Enabled = true;
            private boolean Gradual = true;
        }
    }

    @Getter
    @Setter
    public static class Items {
        private boolean Enabled = true;
        private boolean StackAmount = true;
        private boolean Durability = true;
        private boolean Enchantments = true;
    }
}
