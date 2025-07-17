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

package com.deathmotion.antihealthindicator.commands.antihealthindicator;

import com.deathmotion.antihealthindicator.commands.SubCommand;
import com.deathmotion.antihealthindicator.models.CommonUser;
import com.deathmotion.antihealthindicator.util.CommandComponentCreator;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.List;

public class InfoCommand<P> implements SubCommand<P> {
    private final Component infoComponent;

    public InfoCommand() {
        this.infoComponent = CommandComponentCreator.createAHICommandComponent();
    }

    @Override
    public void execute(CommonUser<P> sender, String[] args) {
        sender.sendMessage(infoComponent);
    }

    @Override
    public List<String> onTabComplete(CommonUser<P> sender, String[] args) {
        return Collections.emptyList();
    }
}
