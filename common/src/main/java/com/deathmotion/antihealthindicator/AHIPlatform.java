package com.deathmotion.antihealthindicator;

public abstract class AHIPlatform<P, S> implements IAHIPlatform<P, S> {

    protected final P platform;

    protected AHIPlatform(P platform) {
        this.platform = platform;
    }

    @Override
    public P getPlatform() {
        return this.platform;
    }

    @Override
    public void onLoad() {
        // Default implementation can be empty or you may place any logic that is common across all platforms.
    }

    @Override
    public void onEnable() {
        // Default implementation can be empty or you may place any logic that is common across all platforms.
    }

    @Override
    public void onDisable() {
        // Default implementation can be empty or you may place any logic that is common across all platforms.
    }

    @Override
    public abstract boolean hasPermission(S sender, String permission);
}