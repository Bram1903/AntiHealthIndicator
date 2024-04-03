package com.deathmotion.antihealthindicator.wrappers.interfaces;

public interface Scheduler<T> {
        T runAsyncTask(Runnable runnable);
}