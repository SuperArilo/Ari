package com.tty.dto;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;


import static com.tty.listener.player.PlayerActionListener.PLAYER_SIT_ACTION_MAP;

public class PlayerSitAction {

    private final Player player;
    private CancellableTask task;
    private ArmorStand armorStand;

    public PlayerSitAction(Player player) {
        this.player = player;
    }

    public void sit(Location location) {

        //判断当前坐的位置上方是否是空气
        if (!location.clone().add(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
            this.player.sendActionBar(TextTool.setHEXColorText("function.sit.error-location", FilePath.Lang));
            this.cancel();
            return;
        }

       this.armorStand = this.player.getWorld().spawn(location, ArmorStand.class, entity -> {
            entity.setInvulnerable(true);
            entity.setGravity(false);
            entity.setSmall(true);
            entity.setMarker(true);
            entity.setInvisible(true);
            entity.setRotation(location.getYaw(), 0);
            entity.addPassenger(this.player);
        });

        this.player.setRotation(location.getYaw(), 0);
        this.player.sendActionBar(TextTool.setHEXColorText("function.sit.tips", FilePath.Lang));
        this.task = Lib.Scheduler.runAtEntityFixedRate(Ari.instance, this.player, i -> {
            if (this.player.getVehicle() instanceof ArmorStand a) {
                Material type = a.getLocation().getBlock().getType();
                if (type.equals(Material.AIR)) {
                    this.cancel();
                    this.player.sendActionBar(TextTool.setHEXColorText("function.sit.error-location", FilePath.Lang));
                    return;
                }
                String name = type.name();
                if (this.player.isDead() || this.player.isFlying() || (!name.endsWith("_STAIRS") && !name.endsWith("_SLAB"))) {
                    this.cancel();
                }
            }
        }, () -> {}, 1L, 20L);
    }

    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
        }
        if (this.armorStand != null) {
            this.armorStand.remove();
        }
        PLAYER_SIT_ACTION_MAP.remove(this.player);
    }

}
