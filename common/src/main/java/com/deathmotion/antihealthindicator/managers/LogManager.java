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
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * LogManager class to handle all kinds of logging messages.
 * This class supports logging info, warning, error and debug messages.
 */
public class LogManager<P> {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-ORX]");

    private final AHIPlatform<P> platform;
    private final Logger logger = Logger.getLogger("AntiHealthIndicator");
    private Boolean debugEnabled;

    public LogManager(AHIPlatform<P> platform) {
        this.platform = platform;
        debugEnabled = null;
    }

    public boolean isDebugEnabled() {
        if (debugEnabled == null) {
            debugEnabled = platform.getConfigurationOption(ConfigOption.DEBUG_ENABLED);
        }
        return debugEnabled;
    }

    protected void log(Level level, @Nullable NamedTextColor color, String message) {
        message = STRIP_COLOR_PATTERN.matcher(message).replaceAll("");
        logger.log(level, color != null ? (color.toString()) : "" + message);
    }

    public void info(String message) {
        log(Level.INFO, null, message);
    }

    public void warn(final String message) {
        log(Level.WARNING, null, message);
    }

    public void severe(String message) {
        log(Level.SEVERE, null, message);
    }

    public void debug(String message) {
        if (this.isDebugEnabled()) {
            log(Level.FINE, null, message);
        }
    }
}