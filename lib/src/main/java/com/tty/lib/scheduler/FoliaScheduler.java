package com.tty.lib.scheduler;

import com.tty.lib.Scheduler;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.task.WrapperScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import java.util.function.Consumer;

import java.util.concurrent.TimeUnit;


public class FoliaScheduler implements Scheduler {
    @Override
    public CancellableTask run(Plugin plugin, Consumer<CancellableTask> task) {
        return new WrapperScheduledTask<>(Bukkit.getGlobalRegionScheduler().run(plugin, i -> task.accept(new WrapperScheduledTask<>(i))));
    }

    @Override
    public CancellableTask runAtEntity(Plugin plugin, Entity entity, Consumer<CancellableTask> task, Runnable errorCallback) {
        return new WrapperScheduledTask<>(entity.getScheduler().run(plugin, i -> task.accept(new WrapperScheduledTask<>(i)), errorCallback));
    }

    @Override
    public CancellableTask runAtEntityFixedRate(Plugin plugin, Entity entity, Consumer<CancellableTask> task, Runnable errorCallback, long delay, long rate) {
        return new WrapperScheduledTask<>(entity.getScheduler().runAtFixedRate(plugin, i -> task.accept(new WrapperScheduledTask<>(i)), errorCallback, delay, rate));
    }

    @Override
    public CancellableTask runAtFixedRate(Plugin plugin, Consumer<CancellableTask> task, long delay, long rate) {
        return new WrapperScheduledTask<>(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, i -> task.accept(new WrapperScheduledTask<>(i)), delay, rate));
    }

    @Override
    public CancellableTask runAsync(Plugin plugin, Consumer<CancellableTask> task) {
        return new WrapperScheduledTask<>(Bukkit.getAsyncScheduler().runNow(plugin, i -> task.accept(new WrapperScheduledTask<>(i))));
    }

    @Override
    public CancellableTask runAsyncAtFixedRate(Plugin plugin, Consumer<CancellableTask> task, long delay, long rate) {
        return new WrapperScheduledTask<>(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, i -> task.accept(new WrapperScheduledTask<>(i)), delay * 50, rate * 50, TimeUnit.MILLISECONDS));
    }

    @Override
    public CancellableTask runAtRegion(Plugin plugin, Location loc, Consumer<CancellableTask> task) {
        return new WrapperScheduledTask<>(Bukkit.getRegionScheduler().run(plugin, loc, i -> task.accept(new WrapperScheduledTask<>(i))));
    }

    @Override
    public CancellableTask runAtRegionLater(Plugin plugin, Location loc, Consumer<CancellableTask> task, long later) {
        return new WrapperScheduledTask<>(Bukkit.getRegionScheduler().runDelayed(plugin, loc, i -> task.accept(new WrapperScheduledTask<>(i)), later));
    }

    @Override
    public CancellableTask runAtRegion(Plugin plugin, World world, int chunkX, int chunkZ, Consumer<CancellableTask> task) {
        return new WrapperScheduledTask<>(Bukkit.getRegionScheduler().run(plugin, world, chunkX, chunkZ, i ->  task.accept(new WrapperScheduledTask<>(i))));
    }

    @Override
    public CancellableTask runDelayed(Plugin plugin, Consumer<CancellableTask> taskConsumer, long delay) {
        return new WrapperScheduledTask<>(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, i -> taskConsumer.accept(new WrapperScheduledTask<>(i)), delay));
    }

    @Override
    public CancellableTask runAsyncDelayed(Plugin plugin, Consumer<CancellableTask> task, long delay) {
        return new WrapperScheduledTask<>(Bukkit.getAsyncScheduler().runDelayed(plugin, i -> task.accept(new WrapperScheduledTask<>(i)), delay * 50, TimeUnit.MILLISECONDS));
    }


    @Override
    public CancellableTask runLater(Plugin plugin, Consumer<CancellableTask> task, long delayTicks) {
        return new WrapperScheduledTask<>(Bukkit.getGlobalRegionScheduler().runDelayed(plugin, i -> task.accept(new WrapperScheduledTask<>(i)), delayTicks));
    }
}
