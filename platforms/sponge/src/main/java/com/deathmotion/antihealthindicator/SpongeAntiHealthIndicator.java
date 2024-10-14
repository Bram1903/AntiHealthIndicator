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
