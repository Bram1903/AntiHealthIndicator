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

package com.deathmotion.antihealthindicator.testplugin;

import com.deathmotion.antihealthindicator.api.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.api.AntiHealthIndicatorAPI;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import org.slf4j.Logger;

public final class ApiTestPlugin {

    private final Logger logger;

    private AntiHealthIndicatorAPI api;

    @Inject
    public ApiTestPlugin(Logger logger) {
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignoredEvent) {
        api = AntiHealthIndicator.getAPI();
        logger.info("Successfully hooked into the AntiHealthIndicator API running version {}", api.getVersion().toStringWithoutSnapshot());
    }
}
