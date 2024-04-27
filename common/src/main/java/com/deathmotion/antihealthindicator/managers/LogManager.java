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

package com.deathmotion.antihealthindicator.managers;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;

import java.util.logging.Logger;

/**
 * LogManager class to handle all kinds of logging messages.
 * This class supports logging info, warning, error and debug messages.
 *
 * @param <P> the platform type.
 */
public final class LogManager<P> {
    private final Logger logger = Logger.getLogger("AntiHealthIndicator");
    private final boolean debugEnabled;

    /**
     * A constructor to initialize LogManager.
     *
     * @param platform AHIPlatform instance with platform-specific configurations.
     */
    public LogManager(AHIPlatform<P> platform) {
        this.debugEnabled = platform.getConfigurationOption(ConfigOption.DEBUG_ENABLED);
    }

    /**
     * Logs the information messages
     *
     * @param message the message to be logged as info
     */
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Logs the warning messages
     *
     * @param message the message to be logged as warning
     */
    public void warning(String message) {
        logger.warning(message);
    }

    /**
     * Logs the error messages
     *
     * @param message the message to be logged as error
     */
    public void error(String message) {
        logger.severe(message);
    }

    /**
     * Logs the debug messages, but only if debug mode is enabled.
     *
     * @param message the message to be logged as debug
     */
    public void debug(String message) {
        if (debugEnabled) {
            logger.info("[DEBUG] " + message);
        }
    }
}