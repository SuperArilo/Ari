package ari.superarilo.function;

import ari.superarilo.Ari;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.function.Consumer;

public class TimeManager {

    private final World world;

    private TimeManager(World world) {
        this.world = world;
    }

    public void timeSet(long tick, long delay, long addTick, Consumer<Long> consumer) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                Ari.instance,
                task -> {
                    long currentTime = this.world.getTime();
                    long delta = (tick - currentTime + 24000) % 24000;
                    if (delta == 0) {
                        if (consumer != null) {
                            consumer.accept(null);
                        }
                        task.cancel();
                        return;
                    }
                    long add = Math.min(delta, addTick);
                    if (add == delta) {
                        if (consumer != null) {
                            consumer.accept(null);
                        }
                        task.cancel();
                        return;
                    }
                    long nowTime = currentTime + add;
                    this.world.setTime(nowTime);
                    if (consumer != null) {
                        consumer.accept(nowTime);
                    }
                },
                delay,
                1L
        );
    }
    public void timeSet(long tick, Consumer<Long> consumer) {
        this.timeSet(tick, 1L, 100, consumer);
    }

    public void timeSet(long targetTimeTick) {
        this.timeSet(targetTimeTick, 1L, 100, null);
    }

    public String tickToTime(long tick) {
        long adjustedTick = tick % 24000;
        int hours = (int) ((adjustedTick / 1000 + 6) % 24);
        int minutes = (int) ((adjustedTick % 1000) * 60 / 1000);
        return String.format("%02d:%02d %s", (hours == 0) ? 12 : hours % 12, minutes, (hours >= 12) ? "PM" : "AM");
    }

    public static TimeManager build(World world) {
        return new TimeManager(world);
    }

}
