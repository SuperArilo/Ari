package com.tty.lib;

import com.tty.lib.task.CancellableTask;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import java.util.function.Consumer;

public interface Scheduler {
    CancellableTask run(Plugin plugin, Consumer<CancellableTask> task);
    CancellableTask runAtEntity(Plugin plugin, Entity entity, Consumer<CancellableTask> task, Runnable errorCallback);
    CancellableTask runAtEntityFixedRate(Plugin plugin, Entity entity, Consumer<CancellableTask> task, Runnable errorCallback, long delay, long rate);
    CancellableTask runAtFixedRate(Plugin plugin, Consumer<CancellableTask> task, long delay, long rate);
    CancellableTask runAsync(Plugin plugin, Consumer<CancellableTask> task);
    CancellableTask runAsyncAtFixedRate(Plugin plugin, Consumer<CancellableTask> task, long c, long rate);
    CancellableTask runAtRegion(Plugin plugin, Location loc, Consumer<CancellableTask> task);
    CancellableTask runAtRegionLater(Plugin plugin, Location loc, Consumer<CancellableTask> task, long later);
    CancellableTask runAtRegion(Plugin plugin, World world, int chunkX, int chunkZ, Consumer<CancellableTask> task);
    CancellableTask runDelayed(Plugin plugin, Consumer<CancellableTask> taskConsumer, long delay);
    CancellableTask runAsyncDelayed(Plugin plugin, Consumer<CancellableTask> task, long delay);
    CancellableTask runLater(Plugin plugin, Consumer<CancellableTask> task, long delayTicks);
}
