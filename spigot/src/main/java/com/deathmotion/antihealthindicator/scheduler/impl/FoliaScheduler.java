package com.deathmotion.antihealthindicator.scheduler.impl;

import com.deathmotion.antihealthindicator.wrappers.interfaces.Scheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;

public class FoliaScheduler implements Scheduler {
    GlobalRegionScheduler globalScheduler = new GlobalRegionScheduler();
}