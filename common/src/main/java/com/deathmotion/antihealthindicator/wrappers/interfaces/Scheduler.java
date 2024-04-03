package com.deathmotion.antihealthindicator.wrappers.interfaces;

import org.jetbrains.annotations.Nullable;

public interface Scheduler<T> {

        T runTaskLater(Runnable runnable, long delay);

        T runTaskTimer(Runnable runnable, long delay, long period);

        T runTask(Runnable runnable);


        T runAsyncTask(Runnable runnable);

        T runAsyncTaskLater(@Nullable Object entity, Runnable runnable, long delay);

        default T runAsyncTaskLater(Runnable runnable, long delay) {
                return runAsyncTaskLater(null, runnable, delay);
        }

        T runAsyncTaskTimer(Runnable runnable, long delay, long period);
}