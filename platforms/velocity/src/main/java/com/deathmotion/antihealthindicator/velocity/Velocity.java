package com.deathmotion.antihealthindicator.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "antihealthindicator",
        name = "AntiHealthIndicator",
        version = "2.1.0",
        description = "Prevent health indicators from being displayed on the client",
        authors = {"Bram"},
        url = "https://github.com/Bram1903/AntiHealthIndicator"
)
public class Velocity {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Hello, Velocity!");
    }
}
