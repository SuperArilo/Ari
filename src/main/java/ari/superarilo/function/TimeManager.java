package ari.superarilo.function;

import ari.superarilo.Ari;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeManager {

    private final World world;

    private TimeManager(World world) {
        this.world = world;
    }

    public void timeSet(long tick) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(Ari.instance, i -> {
            long currentTime = this.world.getTime();
            long delta = (tick - currentTime + 24000) % 24000;
            if (delta == 0) {
                i.cancel();
                return;
            }
            long add = Math.min(delta, 100);
            this.world.setFullTime(this.world.getFullTime() + add);
            if (add == delta) {
                i.cancel();
            }
        }, 1L, 1L);
    }


    public static TimeManager build(World world) {
        return new TimeManager(world);
    }
}
