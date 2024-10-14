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

import com.deathmotion.antihealthindicator.interfaces.Scheduler;
import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;

import java.nio.file.Path;
import java.util.UUID;

public class SpongeAntiHealthIndicator extends AHIPlatform<Platform> {

    private final Path configDirectory;

    @Inject
    public SpongeAntiHealthIndicator(@ConfigDir(sharedRoot = false) Path configDirectory) {
        this.configDirectory = configDirectory;
    }

    protected void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Platform getPlatform() {
        return Sponge.platform();
    }

    @Override
    public boolean hasPermission(UUID sender, String permission) {
        return Sponge.server().player(sender)
                .map(player -> player.hasPermission(permission))
                .orElse(false);
    }


    @Override
    public void sendConsoleMessage(Component message) {
        Sponge.server().sendMessage(message);
    }

    @Override
    public String getPluginDirectory() {
        return this.configDirectory.toAbsolutePath().toString();
    }

}
