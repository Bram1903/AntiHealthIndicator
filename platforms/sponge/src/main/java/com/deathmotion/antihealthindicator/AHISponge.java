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

import com.deathmotion.antihealthindicator.commands.SpongeAHICommand;
import com.deathmotion.antihealthindicator.schedulers.SpongeScheduler;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;

@Plugin("antihealthindicator")
public class AHISponge {

    private final PluginContainer pluginContainer;
    private final Logger logger;
    private final SpongeAntiHealthIndicator ahi;

    @Inject
    public AHISponge(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDirectory, Logger logger) {
        this.pluginContainer = pluginContainer;
        this.logger = logger;
        this.ahi = new SpongeAntiHealthIndicator(configDirectory);
    }

    public SpongeAntiHealthIndicator getAhi() {
        return this.ahi;
    }

    @Listener
    public void onServerStart(final StartedEngineEvent<Server> event) {
        ahi.commonOnInitialize();
        ahi.setScheduler(new SpongeScheduler(this.pluginContainer));
        ahi.commonOnEnable();

        enableBStats();
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Raw> event) {
        event.register(
                this.pluginContainer,
                new SpongeAHICommand(this, logger),
                "antihealthindicator",
                "ahi"
        );
    }

    @Listener
    public void onServerStop(final StoppingEngineEvent<Server> event) {
        ahi.commonOnDisable();
    }

    private void enableBStats() {
    }
}