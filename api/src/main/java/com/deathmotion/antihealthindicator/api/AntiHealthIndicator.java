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

package com.deathmotion.antihealthindicator.api;

/**
 * Singleton manager for the AntiHealthIndicator API.
 */
public final class AntiHealthIndicator {
    private static AntiHealthIndicatorAPI api;

    private AntiHealthIndicator() {
    }

    /**
     * Retrieves the globally available API instance.
     *
     * @return The API instance.
     * @throws IllegalStateException if the API has not been initialized.
     */
    public static AntiHealthIndicatorAPI getAPI() {
        if (api == null) {
            throw new IllegalStateException("AntiHealthIndicator API is not initialized! Ensure the implementation is set before calling getInstance().");
        }
        return api;
    }

    /**
     * Sets the global API instance.
     * Can only be set once.
     *
     * @param instance The api implementation instance.
     * @throws IllegalStateException if the API has already been initialized.
     */
    public static void setAPI(AntiHealthIndicatorAPI instance) {
        if (api != null) {
            throw new IllegalStateException("AntiHealthIndicator API instance is already set and cannot be modified.");
        }
        api = instance;
    }
}
