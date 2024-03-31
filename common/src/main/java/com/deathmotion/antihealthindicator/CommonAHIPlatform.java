package com.deathmotion.antihealthindicator;

public abstract class CommonAHIPlatform {
    public void commonOnLoad() {
        System.out.println("Default loading behavior.");
    }

    public void commonOnEnable() {
        System.out.println("Default enabling behavior.");
    }

    public void commonOnDisable() {
        System.out.println("Default disabling behavior.");
    }
}
