package com.tty.lib.scheduler;

import com.tty.lib.Scheduler;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.task.WrapperScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.function.Consumer;

import java.util.concurrent.atomic.AtomicReference;

public class BukkitScheduler implements Scheduler {
    @Override
    public CancellableTask run(Plugin plugin, Consumer<CancellableTask> task) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTask(plugin, () -> task.accept(atomicReference.get()))));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAtEntity(Plugin plugin, Entity entity, Consumer<CancellableTask> task, Runnable errorCallback) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        BukkitTask bukkitTask = Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                task.accept(new WrapperScheduledTask<>(atomicReference.get()));
            } catch (Exception e) {
                if (errorCallback == null) return;
                errorCallback.run();
            }
        });
        atomicReference.set(new WrapperScheduledTask<>(bukkitTask));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAtEntityFixedRate(Plugin plugin, Entity entity, Consumer<CancellableTask> task, Runnable errorCallback, long delay, long rate) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                task.accept(atomicReference.get());
            } catch (Exception e) {
                if (errorCallback == null) return;
                errorCallback.run();
            }
        }, delay, rate);
        atomicReference.set(new WrapperScheduledTask<>(bukkitTask));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAtFixedRate(Plugin plugin, Consumer<CancellableTask> task, long delay, long rate) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTaskTimer(plugin, () -> task.accept(atomicReference.get()), delay, rate)));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAsync(Plugin plugin, Consumer<CancellableTask> task) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> task.accept(atomicReference.get()))));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAsyncAtFixedRate(Plugin plugin, Consumer<CancellableTask> task, long delay, long rate) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> task.accept(atomicReference.get()), delay, rate)));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAtRegion(Plugin plugin, Location loc, Consumer<CancellableTask> task) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTask(plugin, () -> task.accept(atomicReference.get()))));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAtRegionLater(Plugin plugin, Location loc, Consumer<CancellableTask> task, long later) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(atomicReference.get()), later)));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAtRegion(Plugin plugin, World world, int chunkX, int chunkZ, Consumer<CancellableTask> task) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTask(plugin, () -> task.accept(atomicReference.get()))));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runDelayed(Plugin plugin, Consumer<CancellableTask> task, long delay) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(atomicReference.get()), delay)));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runAsyncDelayed(Plugin plugin, Consumer<CancellableTask> task, long delay) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> task.accept(atomicReference.get()), delay)));
        return atomicReference.get();
    }

    @Override
    public CancellableTask runLater(Plugin plugin, Consumer<CancellableTask> task, long delayTicks) {
        AtomicReference<WrapperScheduledTask<BukkitTask>> atomicReference = new AtomicReference<>();
        atomicReference.set(new WrapperScheduledTask<>(Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(atomicReference.get()), delayTicks)));
        return atomicReference.get();
    }
}
