package com.tty.lib.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitTask;

public record WrapperScheduledTask<T>(T task) implements CancellableTask {

    @Override
    public void cancel() {
        switch (task) {
            case null -> throw new IllegalStateException("Task not initialized");
            case BukkitTask bukkitTask -> bukkitTask.cancel();
            case ScheduledTask scheduledTask -> scheduledTask.cancel();
            default -> {
            }
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
