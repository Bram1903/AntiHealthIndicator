package com.deathmotion.antihealthindicator.schedulers;

import com.deathmotion.antihealthindicator.schedulers.impl.BukkitScheduler;
import com.deathmotion.antihealthindicator.schedulers.impl.FoliaScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public class Scheduler {

    public ServerScheduler getScheduler(JavaPlugin plugin) {
        if (isFolia()) {
            return new FoliaScheduler(plugin);
        }

        return new BukkitScheduler(plugin);
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}