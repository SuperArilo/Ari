package com.tty.function;

public interface TeleportCallback {
    default void before(TeleportThread teleportThread) {}
    default void after() {}
    default void onCancel() {}
}
