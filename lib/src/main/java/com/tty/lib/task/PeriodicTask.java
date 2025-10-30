package com.tty.lib.task;

import com.tty.lib.Lib;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PeriodicTask {

    private final List<Runnable> tasks = new ArrayList<>();

    private final JavaPlugin plugin;
    @Setter
    private long c;
    @Setter
    private long rate;

    public PeriodicTask(long rate, long c, JavaPlugin plugin) {
        this.rate = rate;
        this.c = c;
        this.plugin = plugin;
    }


    private CancellableTask cancellableTask;

    public void start() {
        this.cancellableTask = Lib.Scheduler.runAsyncAtFixedRate(this.plugin, (i) -> this.execute(), c, rate);
    }

    public void stop() {
        if(this.cancellableTask == null) return;
        this.cancellableTask.cancel();
        this.cancellableTask = null;
    }

    private void execute() {
        if(tasks.isEmpty()) return;
        tasks.forEach(Runnable::run);
    }

    public void addTask(Runnable task) {
        tasks.add(task);
    }

    public boolean removeTask(Runnable task) {
        return tasks.remove(task);
    }
}
