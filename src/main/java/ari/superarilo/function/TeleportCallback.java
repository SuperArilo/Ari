package ari.superarilo.function;

public interface TeleportCallback {
    default void before(TeleportThread teleportThread) {}
    default void after() {}
}
