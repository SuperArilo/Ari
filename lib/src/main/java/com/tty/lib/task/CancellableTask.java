package com.tty.lib.task;

public interface CancellableTask {
    void cancel();
    boolean isCancelled();
}
