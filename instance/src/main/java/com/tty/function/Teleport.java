package com.tty.function;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.ServerPlatform;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Teleport {

    private final String title;
    private final String subTitle;

    private final Player player;
    private final Location targetLocation;

    private final int delay;
    protected boolean status = true;

    private final Location initLocation;
    private final double initHealth;

    protected Consumer<Teleport> before;
    protected Runnable after = () -> {};
    protected Runnable aborted = () -> {};


    protected Teleport(Player player, Location targetLocation, int delay) {
        this.player = player;
        this.initHealth = player.getHealth();
        this.initLocation = player.getLocation();
        this.targetLocation = targetLocation;
        this.delay = Math.max(delay, 0);

        this.title = ConfigUtils.getValue("teleport.title.main", FilePath.Lang);
        this.subTitle = ConfigUtils.getValue("teleport.title.sub-title", FilePath.Lang);

    }
    public Teleport aborted(Runnable runnable) {
        this.aborted = runnable;
        return this;
    }
    public Teleport before(Consumer<Teleport> consumer) {
        this.before = consumer;
        return this;
    }

    public void after(Runnable runnable) {
        this.after = runnable;
    }

    public Teleport teleport() {
        if (this.before != null) {
            this.before.accept(this);
        }
        if(!this.status) return this;
        AtomicInteger timerIndex = new AtomicInteger();
        if (this.player.isOp()) {
            timerIndex.set(0);
        } else {
            timerIndex.set(delay);
            this.player.sendMessage(ConfigUtils.t("teleport.ing"));
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
                threadPlayer.sendMessage(ConfigUtils.t("teleport.break"));
                return;
            }

            if (timerIndex.get() > 0) {
                threadPlayer.showTitle(ComponentUtils.setPlayerTitle(
                        this.title,
                        this.subTitle.replace(LangType.TELEPORTDELAY.getType(), String.valueOf(timerIndex.get())),
                        200,
                        1000,
                        200));
                timerIndex.decrementAndGet();
                return;
            } else {
                threadPlayer.clearTitle();
            }
            t.cancel();
            Lib.Scheduler.runAtRegion(Ari.instance, this.targetLocation, i -> {
                for (int y = 0;y <= this.targetLocation.getWorld().getMaxHeight();y++) {
                    if (this.targetLocation.clone().add(0, y, 0).getBlock().isEmpty()) {
                        this.targetLocation.add(0, y, 0);
                        break;
                    }
                }
                threadPlayer.teleportAsync(this.targetLocation,
                                PlayerTeleportEvent.TeleportCause.PLUGIN)
                        .thenAccept(p -> {
                            if (p) {
                                if (ServerPlatform.isFolia()) {
                                    Bukkit.getPluginManager().callEvent(new PlayerTeleportEvent(threadPlayer, this.initLocation,this.targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN));
                                }
                                threadPlayer.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f));
                            }
                            this.after.run();
                            threadPlayer.sendMessage(ConfigUtils.t(p ? "teleport.success":"function.tpa.error"));
                        });
            });
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
        return entity.getHealth() < this.initHealth;
    }

    /**
     * 检查是否移动
     * @param entity 被检查的实体
     */
    protected boolean hasMoved(Damageable entity) {
        Location currentLocation = entity.getLocation();
        return makePositive(this.initLocation.getX() - currentLocation.getX()) + makePositive(this.initLocation.getY() - currentLocation.getY()) + makePositive(this.initLocation.getZ() - currentLocation.getZ()) > 0.1;
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
