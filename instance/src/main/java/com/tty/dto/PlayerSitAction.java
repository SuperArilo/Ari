package com.tty.dto;

import com.tty.Ari;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.Log;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;

import static com.tty.listener.player.PlayerActionListener.PLAYER_SIT_ACTION_MAP;

public class PlayerSitAction {

    private final Player player;
    private CancellableTask task;
    private AreaEffectCloud cloud;

    public PlayerSitAction(Player player) {
        this.player = player;
    }

    public boolean sit(Location location) {

       this.cloud = this.player.getWorld().spawn(location, AreaEffectCloud.class, entity -> {
            entity.setInvulnerable(true);
            entity.setGravity(false);
            entity.setInvisible(true);
            entity.setRadius(0);
            entity.addPassenger(this.player);
        });

        this.player.setRotation(location.getYaw(), 0);
        this.player.sendActionBar(TextTool.setHEXColorText("function.sit.tips", FilePath.Lang));
        this.task = Lib.Scheduler.runAtEntityFixedRate(Ari.instance, this.player, i -> {
            if (this.player.isInsideVehicle() && this.player.getVehicle() instanceof AreaEffectCloud a) {
                Location check_1 = a.getLocation();
                Location check_2 = a.getLocation().clone().subtract(0, 0.5, 0);

                boolean air1 = check_1.getBlock().getType().isAir();
                boolean air2 = check_2.getBlock().getType().isAir();

                if (air1 && air2) {
                    this.player.sendActionBar(TextTool.setHEXColorText("function.sit.error-location", FilePath.Lang));
                    this.cancel();
                    return;
                }

                Material type = air1 ? check_2.getBlock().getType():check_1.getBlock().getType();

                String name = type.name();
                if (this.player.isDead() || this.player.isFlying() || !this.player.isOnline() || (!name.endsWith("_STAIRS") && !name.endsWith("_SLAB"))) {
                    this.cancel();
                }
            } else {
                this.cancel();
                Log.debug("sit length: " + PLAYER_SIT_ACTION_MAP.size());
            }
        }, () -> {}, 1L, 20L);
        return true;
    }

    public void cancel() {
        if (this.cloud == null) return;
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        this.player.leaveVehicle();
        this.cloud.remove();
        PLAYER_SIT_ACTION_MAP.remove(this.player);
        //同步移除实体和sit状态
        if (!this.player.isOnline()) {
            this.player.saveData();
        }
    }
}
