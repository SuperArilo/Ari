package com.tty.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.tool.Log;
import com.tty.tool.TextTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Teleport {

    private final Player player;
    private final Location targetLocation;

    private final int delay;
    protected boolean status = true;

    private final Location entityInitLocation;
    private final double entityInitHealth;

    protected Consumer<Teleport> before;
    protected Runnable after = () -> {};
    protected Runnable aborted = () -> {};


    protected Teleport(Player player, Location targetLocation, int delay) {
        this.player = player;
        this.entityInitHealth = player.getHealth();
        this.entityInitLocation = player.getLocation();
        this.targetLocation = targetLocation;
        this.delay = delay;
    }
    public Teleport aborted(Runnable runnable) {
        this.aborted = runnable;
        return this;
    }
    public Teleport before(Consumer<Teleport> consumer) {
        this.before = consumer;
        return this;
    }

    public Teleport after(Runnable runnable) {
        runnable.run();
        return this;
    }

    public Teleport teleport() {
        if (this.before != null) {
            this.before.accept(this);
        }
        AtomicInteger timerIndex = new AtomicInteger();
        if (this.player.isOp()) {
            timerIndex.set(1);
        } else {
            timerIndex.set(delay);
            this.player.sendMessage(TextTool.setHEXColorText("teleport.ing", FilePath.Lang));
        }
        Lib.Scheduler.runAsyncAtFixedRate(Ari.instance,t -> {
            if (!this.status) {
                t.cancel();
                this.aborted.run();
                return;
            }
            Player threadPlayer = Bukkit.getServer().getPlayer(this.player.getUniqueId());
            if (threadPlayer == null) {
                t.cancel();
                this.aborted.run();
                return;
            }
            //判断玩家是否在传送过程中移动或者受伤
            if (this.hasMoved(threadPlayer) || this.hasLostHealth(threadPlayer)) {
                t.cancel();
                this.aborted.run();
                threadPlayer.sendMessage(TextTool.setHEXColorText("teleport.break", FilePath.Lang));
                return;
            }

            Log.debug("start time: " + timerIndex.get() + "s");

            timerIndex.decrementAndGet();
            if (timerIndex.get() < 0) {
                timerIndex.set(0);
            }

            if (timerIndex.get() != 0) return;
            t.cancel();
            Lib.Scheduler.runAtEntity(Ari.instance,
                    threadPlayer,
                    i -> {
                        for (int y = 0;y <= this.targetLocation.getWorld().getMaxHeight();y++) {
                            if (this.targetLocation.clone().add(0, y, 0).getBlock().isEmpty()) {
                                this.targetLocation.add(0, y, 0);
                                break;
                            }
                        }
                        threadPlayer.teleportAsync(this.targetLocation,
                                PlayerTeleportEvent.TeleportCause.PLUGIN)
                        .thenAccept(p -> {
                            this.after.run();
                            threadPlayer.sendMessage(TextTool.setHEXColorText(p ? "teleport.success":"function.tpa.error", FilePath.Lang));
                        });
                    },
                    () -> Log.error("teleport error on player: " + threadPlayer.getName()));
            }, 0, 20);
        return this;
    }

    public void cancel() {
        this.status = false;
    }
    /**
     * 检查是否受伤
     * @param entity 被检查的实体
     */
    private boolean hasLostHealth(Damageable entity) {
        return entity.getHealth() < this.entityInitHealth;
    }

    /**
     * 检查是否移动
     * @param entity 被检查的实体
     */
    protected boolean hasMoved(Damageable entity) {
        Location currentLocation = entity.getLocation();
        return makePositive(this.entityInitLocation.getX() - currentLocation.getX()) + makePositive(this.entityInitLocation.getY() - currentLocation.getY()) + makePositive(this.entityInitLocation.getZ() - currentLocation.getZ()) > 0.1;
    }

    protected double makePositive(double d) {
        if (d < 0) {
            d = d * -1D;
        }
        return d;
    }

    public static Teleport create(Player player, Location targetLocation, int delay) {
        return new Teleport(player, targetLocation, delay);
    }

}
