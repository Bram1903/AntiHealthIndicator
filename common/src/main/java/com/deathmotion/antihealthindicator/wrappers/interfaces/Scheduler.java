package com.deathmotion.antihealthindicator.wrappers.interfaces;

public interface Scheduler<T> {

    /**
     * Schedule a task to be executed synchronously.
     *
     * @param task the task to execute
     */
    T runTask(Runnable task);

    /**
     * Schedule a task to be executed asynchronously.
     *
     * @param task the task to execute
     */
    T runTaskAsynchronously(Runnable task);

    /**
     * Schedule a task to be executed after a specified delay.
     *
     * @param task the task to execute
     * @param delay the delay in server ticks before executing the task
     */
    T runTaskLater(Runnable task, long delay);

    /**
     * Schedule a task to be executed at a fixed rate.
     *
     * @param task the task to execute
     * @param delay the delay in server ticks before executing the first repetition
     * @param period the number of server ticks to wait between ending and starting the task again
     */
    T runTaskTimer(Runnable task, long delay, long period);

    /**
     * Schedule a task to be executed asynchronously at a fixed rate.
     *
     * @param task the task to execute
     * @param delay the delay in server ticks before executing the first repetition
     * @param period the number of server ticks to wait between ending and starting the task again
     */
    T runTaskTimerAsynchronously(Runnable task, long delay, long period);
}
