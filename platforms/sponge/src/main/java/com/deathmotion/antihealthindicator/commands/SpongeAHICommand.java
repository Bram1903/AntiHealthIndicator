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

package com.deathmotion.antihealthindicator.commands;

import com.deathmotion.antihealthindicator.AHISponge;
import com.deathmotion.antihealthindicator.data.CommonUser;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Platform;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpongeAHICommand implements Command.Raw {
    private final AHISponge plugin;

    public SpongeAHICommand(AHISponge plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public CommandResult process(@NotNull CommandCause cause, @NotNull ArgumentReader.Mutable arguments) {
        plugin.getAhi().getCommand().onCommand(createCommonUser(cause), assimilateArguments(arguments));
        return CommandResult.success();
    }

    @Override
    @NotNull
    public List<CommandCompletion> complete(@NotNull CommandCause cause, @NotNull ArgumentReader.Mutable arguments) {
        List<String> completions = plugin.getAhi().getCommand().onTabComplete(createCommonUser(cause), assimilateArguments(arguments));

        // Convert List<String> to List<CommandCompletion>
        return completions.stream()
                .map(CommandCompletion::of)
                .collect(Collectors.toList());
    }

    /**
     * Assimilate the arguments from a {@link ArgumentReader} into a spaced {@link String} array.
     *
     * @param arguments the {@link ArgumentReader} to assimilate
     * @return the assimilated {@link String} array
     */
    @NotNull
    private String[] assimilateArguments(@NotNull ArgumentReader.Mutable arguments) {
        final String argumentString = arguments.immutable().remaining();
        if (argumentString.trim().isEmpty()) {
            return new String[0];
        }
        return argumentString.split(" ");
    }

    @Override
    public boolean canExecute(@NotNull CommandCause cause) {
        return true;
    }

    @Override
    @NotNull
    public Optional<Component> shortDescription(@NotNull CommandCause cause) {
        return Optional.of(Component.text("Base command for AntiHealthIndicator."));
    }

    @Override
    public Optional<Component> extendedDescription(CommandCause cause) {
        return Optional.of(Component.text("Base command for AntiHealthIndicator."));
    }

    @Override
    public Component usage(CommandCause cause) {
        return Component.text("/antihealthindicator");
    }

    private CommonUser<Platform> createCommonUser(CommandCause sender) {
        if (sender.root() instanceof ServerPlayer) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(sender.root());
            plugin.getAhi().getLogManager().info("User: " + user.getName());
            return new CommonUser<>(plugin.getAhi(), user);
        } else {
            plugin.getAhi().getLogManager().info("Console");
            return new CommonUser<>(plugin.getAhi(), null);
        }
    }
}
