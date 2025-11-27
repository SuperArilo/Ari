package com.tty.function;

import com.tty.Ari;
import com.tty.lib.Lib;
import com.tty.lib.ServerPlatform;
import com.tty.tool.ConfigUtils;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.function.Consumer;

public class Teleporting {


    private final Entity entity;
    private final Location targetLocation;
    protected boolean status = true;

    private final Location initLocation;

    protected Consumer<Teleporting> before;
    protected Runnable after = () -> {};
    protected Runnable aborted = () -> {};


    protected Teleporting(Entity entity, Location targetLocation) {
        this.entity = entity;
        this.initLocation = entity.getLocation();
        this.targetLocation = targetLocation;

    }
    public Teleporting aborted(Runnable runnable) {
        this.aborted = runnable;
        return this;
    }

    public Teleporting before(Consumer<Teleporting> consumer) {
        this.before = consumer;
        return this;
    }

    public void after(Runnable runnable) {
        this.after = runnable;
    }

    public Teleporting teleport() {
        if (this.before != null) {
            this.before.accept(this);
        }
        if (!this.status) {
            this.aborted.run();
            return this;
        }
        Lib.Scheduler.runAtRegion(Ari.instance, this.targetLocation, i -> {
            for (int y = 0;y <= this.targetLocation.getWorld().getMaxHeight();y++) {
                if (this.targetLocation.clone().add(0, y, 0).getBlock().isEmpty()) {
                    this.targetLocation.add(0, y, 0);
                    break;
                }
            }
            this.entity.teleportAsync(this.targetLocation,
                            PlayerTeleportEvent.TeleportCause.PLUGIN)
                    .thenAccept(p -> {
                        if (p) {
                            if (ServerPlatform.isFolia() && this.entity instanceof Player player) {
                                Bukkit.getPluginManager().callEvent(new PlayerTeleportEvent(player, this.initLocation,this.targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN));
                            }
                            this.entity.playSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f));
                        }
                        this.after.run();
                        this.entity.sendMessage(ConfigUtils.t(p ? "teleport.success":"function.tpa.error"));
                    });
        });
        return this;
    }

    public void cancel() {
        this.status = false;
    }

    public static Teleporting create(Entity entity, Location targetLocation) {
        return new Teleporting(entity, targetLocation);
    }

}
