package com.tty.entity;

import com.tty.tool.Log;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TabTask implements Consumer<ScheduledTask> {
    @Override
    public void accept(ScheduledTask scheduledTask) {
        Log.debug("执行");
    }

    @NotNull
    @Override
    public Consumer<ScheduledTask> andThen(@NotNull Consumer<? super ScheduledTask> after) {
        return Consumer.super.andThen(after);
    }
}
