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

package com.deathmotion.antihealthindicator.enums;

import lombok.Getter;

@Getter
public enum ConfigOption {
    DEBUG_ENABLED("debug.enabled", false),

    UPDATE_CHECKER_ENABLED("update-checker.enabled", true),
    UPDATE_CHECKER_PRINT_TO_CONSOLE("update-checker.print-to-console", true),
    NOTIFY_IN_GAME("update-checker.notify-in-game", true),

    ALLOW_BYPASS_ENABLED("allow-bypass.enabled", false),

    SPOOF_WORLD_SEED_ENABLED("spoof.world-seed.enabled", false),
    SPOOF_FOOD_SATURATION_ENABLED("spoof.food-saturation.enabled", true),

    ENTITY_DATA_ENABLED("spoof.entity-data.enabled", true),
    AIR_TICKS_ENABLED("spoof.entity-data.air-ticks.enabled", true),
    HEALTH_ENABLED("spoof.entity-data.health.enabled", true),

    IGNORE_VEHICLES_ENABLED("spoof.entity-data.health.ignore-vehicles", true),
    IGNORE_WOLVES_ENABLED("spoof.entity-data.health.ignore-wolves.enabled", true),
    FOR_TAMED_WOLVES_ENABLED("spoof.entity-data.health.ignore-wolves.when.for-tamed-wolves", false),
    FOR_OWNED_WOLVES_ENABLED("spoof.entity-data.health.ignore-wolves.when.for-owned-wolves", true),
    IGNORE_IRON_GOLEMS_ENABLED("spoof.entity-data.health.ignore-iron-golems.enabled", true),
    GRADUAL_IRON_GOLEM_HEALTH_ENABLED("spoof.entity-data.health.ignore-iron-golems.gradual.enabled", true),

    ABSORPTION_ENABLED("spoof.entity-data.absorption.enabled", true),
    XP_ENABLED("spoof.entity-data.xp.enabled", true),

    ITEMS_ENABLED("spoof.entity-data.items.enabled", true),
    STACK_AMOUNT_ENABLED("spoof.entity-data.items.stack-amount.enabled", true),
    DURABILITY_ENABLED("spoof.entity-data.items.durability.enabled", true),
    ENCHANTMENTS_ENABLED("spoof.entity-data.items.enchantments.enabled", true);

    private final String key;
    private final boolean defaultValue;

    ConfigOption(String key, boolean defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }
}