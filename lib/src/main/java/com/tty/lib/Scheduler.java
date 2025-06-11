package com.tty.lib;

import com.tty.lib.task.CancellableTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;

public interface Scheduler {
    CancellableTask run(Plugin plugin, Consumer<CancellableTask> task);
    CancellableTask runAtEntity(Plugin plugin, Entity entity, Consumer<CancellableTask> task, Runnable errorCallback);
    CancellableTask runAtFixedRate(Plugin plugin, Consumer<CancellableTask> task, long delay, long rate);
    CancellableTask runAsync(Plugin plugin, Consumer<CancellableTask> task);
    CancellableTask runAsyncAtFixedRate(Plugin plugin, Consumer<CancellableTask> task, long c, long rate);
    CancellableTask runAtRegion(Plugin plugin, Location loc, Consumer<CancellableTask> task);
    CancellableTask runDelayed(Plugin plugin, Consumer<CancellableTask> taskConsumer, long delay);
    CancellableTask runAsyncDelayed(Plugin plugin, Consumer<CancellableTask> task, long delay);
    CancellableTask runLater(Plugin plugin, Consumer<CancellableTask> task, long delayTicks);
}
