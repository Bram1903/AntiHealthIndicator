package com.deathmotion.antihealthindicator.commands;

import com.deathmotion.antihealthindicator.util.ComponentCreator;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;

public class VelocityAHICommand implements SimpleCommand {

    public VelocityAHICommand(ProxyServer proxyServer) {
        CommandManager manager = proxyServer.getCommandManager();
        manager.register(manager.metaBuilder("antihealthindicator").aliases("ahi").build(), this);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!source.hasPermission("AntiHealthIndicator.Version")) {
            source.sendPlainMessage("Unknown command. Type \"/help\" for help.");
            return;
        }

        source.sendMessage(ComponentCreator.createAHICommandComponent());
    }
}