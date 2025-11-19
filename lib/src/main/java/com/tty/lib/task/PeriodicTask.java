package com.tty.lib.task;

import com.tty.lib.Lib;
import com.tty.lib.enum_type.PeriodicTaskEnum;
import com.tty.lib.tool.Log;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PeriodicTask {

    private final PeriodicTaskEnum periodicTaskEnum;
    private final List<Runnable> tasks = new ArrayList<>();

    private final JavaPlugin plugin;
    @Setter
    private long c;
    @Setter
    private long rate;

    public PeriodicTask(PeriodicTaskEnum periodicTaskEnum, long rate, long c, JavaPlugin plugin) {
        this.periodicTaskEnum = periodicTaskEnum;
        this.rate = rate;
        this.c = c;
        this.plugin = plugin;
    }


    private CancellableTask cancellableTask;

    public void start() {
        Log.debug("Player Save Task: start: " + this.rate + " tick");
        this.cancellableTask = Lib.Scheduler.runAsyncAtFixedRate(this.plugin, (i) -> this.execute(), c, rate);
    }

    public void stop() {
        if(this.cancellableTask == null) return;
        this.cancellableTask.cancel();
        Log.debug("stop all periodic task: " + this.periodicTaskEnum.getName());
        this.cancellableTask = null;
    }

    public void reload() {
        this.stop();
        this.tasks.forEach(Runnable::run);
        this.start();
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
