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
    public boolean Debug = false;

    @Getter
    @Setter
    public class UpdateChecker {
        public boolean Enabled = true;
        public boolean PrintToConsole = true;
        public boolean NotifyInGame = true;
    }

    public boolean AllowBypass = false;
    public boolean WorldSeed = false;
    public boolean FoodSaturation = true;

    @Getter
    @Setter
    public class EntityData {
        public boolean Enabled = true;
        public boolean PlayersOnly = false;
        public boolean AirTicks = true;

        public boolean Health = true;
        public boolean IgnoreVehicles = true;

        @Getter
        @Setter
        public class Wolves {
            public boolean Enabled = true;
            public boolean Tamed = false;
            public boolean Owner = true;
        }

        @Getter
        @Setter
        public class IronGolems {
            public boolean Enabled = true;
            public boolean Gradual = true;
        }
    }

    public boolean Absorption = true;
    public boolean Xp = true;

    @Getter
    @Setter
    public class Items {
        public boolean Enabled = true;
        public boolean StackAmount = true;
        public boolean Durability = true;
        public boolean Enchantments = true;
    }
}
