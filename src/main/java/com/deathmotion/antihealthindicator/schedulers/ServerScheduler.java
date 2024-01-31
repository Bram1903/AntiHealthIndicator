package com.deathmotion.antihealthindicator.schedulers;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface ServerScheduler {

    /**
     * Run the task.
     *
     * @param task     task...
     * @param location required for Folia, in Bukkit can be null
     * @return The created {@link ServerScheduler}.
     */
    @NotNull ServerScheduler runTask(Location location, Runnable task);

    /**
     * Run the task asynchronously.
     *
     * @param task task...
     * @return The created {@link ServerScheduler}
     */
    @NotNull ServerScheduler runTaskAsynchronously(Runnable task);

    /**
     * Run the task after a specified number of ticks.
     *
     * @param location required for Folia, in Bukkit can be null
     * @param task     task...
     * @param delay    The number of ticks to wait.
     * @return The created {@link ServerScheduler}
     */
    @NotNull ServerScheduler runTaskLater(Location location, long delay, Runnable task);

    /**
     * Run the task asynchronously after a specified number of ticks.
     *
     * @param task  task...
     * @param delay The number of ticks to wait.
     * @return The created {@link ServerScheduler}
     */
    @NotNull ServerScheduler runTaskLaterAsynchronously(long delay, Runnable task);

    /**
     * Run the task repeatedly on a timer.
     *
     * @param location required for Folia, in Bukkit can be null
     * @param task     task...
     * @param delay    The delay before the task is first run (in ticks).
     * @param period   The ticks elapsed before the task is run again.
     * @return The created {@link ServerScheduler}
     */
    @NotNull ServerScheduler runTaskTimer(Location location, long delay, long period, Runnable task);

    /**
     * Run the task repeatedly on a timer asynchronously.
     *
     * @param task   task...
     * @param delay  The delay before the task is first run (in ticks).
     * @param period The ticks elapsed before the task is run again.
     * @return The created {@link ServerScheduler}
     */
    @NotNull ServerScheduler runTaskTimerAsynchronously(long delay, long period, Runnable task);

    /**
     * Cancel the task.
     */
    void cancel();
}
