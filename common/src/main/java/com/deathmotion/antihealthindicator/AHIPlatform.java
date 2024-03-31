package com.deathmotion.antihealthindicator;

import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.CacheManager;
import com.deathmotion.antihealthindicator.managers.PacketManager;
import com.deathmotion.antihealthindicator.managers.UpdateManager;
import com.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class AHIPlatform<P>  {
    private CacheManager<P> cacheManager;

    public abstract P getPlatform();

    public abstract void onLoad();

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract boolean hasPermission(UUID sender, String permission);

    public abstract boolean getConfigurationOption(ConfigOption option);

    public void commonOnLoad() {
        System.out.println("Default loading behavior.");
    }

    public void commonOnEnable() {
        cacheManager = new CacheManager<>(this);

        new UpdateManager<>(this);
        new PacketManager<>(this);
    }

    public void commonOnDisable() {
        PacketEvents.getAPI().terminate();
    }
}