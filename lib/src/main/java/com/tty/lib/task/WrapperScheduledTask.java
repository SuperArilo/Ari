package com.tty.lib.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitTask;

public record WrapperScheduledTask<T>(T task) implements CancellableTask {

    @Override
    public void cancel() {
        if (task == null) {
            throw new IllegalStateException("Task not initialized");
        }
        if (task instanceof BukkitTask bukkitTask) {
            bukkitTask.cancel();
        } else if (task instanceof ScheduledTask scheduledTask) {
            scheduledTask.cancel();
        }
    }

    @Override
    public boolean isCancelled() {
        if (task instanceof BukkitTask bukkitTask) {
            return bukkitTask.isCancelled();
        } else if (task instanceof ScheduledTask scheduledTask) {
            return scheduledTask.isCancelled();
        }
        return false;
    }

}
