package com.deathmotion.antihealthindicator.wrappers.interfaces;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface Scheduler {
    void runAsyncTask(Consumer<Object> task);

    void rynAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit);
}