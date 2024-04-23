/*
 * This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.schedulers.folia;

import com.github.retrooper.packetevents.util.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Represents a scheduler for executing entity tasks.
 */
public class EntityScheduler {
    private final boolean isFolia = FoliaCompatUtil.isFolia();

    private Method getEntitySchedulerMethod;

    protected EntityScheduler() {
        if (isFolia) {
            getEntitySchedulerMethod = Reflection.getMethod(Entity.class, "getScheduler", 0);
        }
    }

    /**
     * Schedules a task with the given delay. If the task failed to schedule because the scheduler is retired (entity removed), then returns false.
     * Otherwise, either the run callback will be invoked after the specified delay, or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity,
     * remove other entities, load chunks, load worlds, modify ticket levels, etc.
     * <p>
     * It is guaranteed that the run and retired callback are invoked on the region which owns the entity.
     *
     * @param plugin  Plugin which owns the specified task.
     * @param run     The callback to run after the specified delay, may not be null.
     * @param retired Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param delay   The delay in ticks before the run callback is invoked. Any value less-than 1 is treated as 1.
     */
    public void execute(@NotNull Entity entity, @NotNull Plugin plugin, @NotNull Runnable run, @Nullable Runnable retired, long delay) {
        if (delay < 1) delay = 1;

        if (!isFolia) {
            Bukkit.getScheduler().runTaskLater(plugin, run, delay);
            return;
        }

        try {
            Object entityScheduler = getEntitySchedulerMethod.invoke(entity);
            Class<?> entitySchedulerClass = entityScheduler.getClass();
            Method entityExecuteMethod = entitySchedulerClass.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, long.class);

            entityExecuteMethod.invoke(entityScheduler, plugin, run, retired, delay);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedules a task to execute on the next tick. If the task failed to schedule because the scheduler is retired (entity removed),
     * then returns null. Otherwise, either the task callback will be invoked after the specified delay,
     * or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity,
     * remove other entities, load chunks, load worlds, modify ticket levels, etc.
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     *
     * @param plugin  The plugin that owns the task
     * @param task    The task to execute
     * @param retired Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public TaskWrapper run(@NotNull Entity entity, @NotNull Plugin plugin, @NotNull Consumer<Object> task, @Nullable Runnable retired) {
        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTask(plugin, () -> task.accept(null)));
        }

        try {
            Object entityScheduler = getEntitySchedulerMethod.invoke(entity);
            Class<?> entitySchedulerClass = entityScheduler.getClass();
            Method entityRunMethod = entitySchedulerClass.getMethod("run", Plugin.class, Consumer.class, Runnable.class);

            return new TaskWrapper(entityRunMethod.invoke(entityScheduler, plugin, task, retired));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Schedules a task with the given delay. If the task failed to schedule because the scheduler is retired (entity removed),
     * then returns null. Otherwise, either the task callback will be invoked after the specified delay, or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity,
     * remove other entities, load chunks, load worlds, modify ticket levels, etc.
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     *
     * @param plugin     The plugin that owns the task
     * @param task       The task to execute
     * @param retired    Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param delayTicks The delay in ticks before the run callback is invoked. Any value less-than 1 is treated as 1.
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public TaskWrapper runDelayed(@NotNull Entity entity, @NotNull Plugin plugin, @NotNull Consumer<Object> task, @Nullable Runnable retired, long delayTicks) {
        if (delayTicks < 1) delayTicks = 1;

        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(null), delayTicks));
        }

        try {
            Object entityScheduler = getEntitySchedulerMethod.invoke(entity);
            Class<?> entitySchedulerClass = entityScheduler.getClass();
            Method entityRunDelayedMethod = entitySchedulerClass.getMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class, long.class);

            return new TaskWrapper(entityRunDelayedMethod.invoke(entityScheduler, plugin, task, retired, delayTicks));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Schedules a repeating task with the given delay and period. If the task failed to schedule because the scheduler is retired (entity removed),
     * then returns null. Otherwise, either the task callback will be invoked after the specified delay, or the retired callback will be invoked if the scheduler is retired.
     * Note that the retired callback is invoked in critical code, so it should not attempt to remove the entity,
     * remove other entities, load chunks, load worlds, modify ticket levels, etc.
     * <p>
     * It is guaranteed that the task and retired callback are invoked on the region which owns the entity.
     *
     * @param plugin            The plugin that owns the task
     * @param task              The task to execute
     * @param retired           Retire callback to run if the entity is retired before the run callback can be invoked, may be null.
     * @param initialDelayTicks The initial delay, in ticks before the method is invoked. Any value less-than 1 is treated as 1.
     * @param periodTicks       The period, in ticks.
     * @return {@link TaskWrapper} instance representing a wrapped task
     */
    public TaskWrapper runAtFixedRate(@NotNull Entity entity, @NotNull Plugin plugin, @NotNull Consumer<Object> task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
        if (initialDelayTicks < 1) initialDelayTicks = 1;

        if (!isFolia) {
            return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(null), initialDelayTicks, periodTicks));
        }

        try {
            Object entityScheduler = getEntitySchedulerMethod.invoke(entity);
            Class<?> entitySchedulerClass = entityScheduler.getClass();
            Method entityRunAtFixedRateMethod = entitySchedulerClass.getMethod("runAtFixedRate", Plugin.class, Consumer.class, Runnable.class, long.class, long.class);

            return new TaskWrapper(entityRunAtFixedRateMethod.invoke(entityScheduler, plugin, task, retired, initialDelayTicks, periodTicks));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}