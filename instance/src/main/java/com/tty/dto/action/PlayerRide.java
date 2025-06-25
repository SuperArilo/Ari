package com.tty.dto.action;

import com.tty.Ari;
import com.tty.dto.BaseAction;
import com.tty.lib.Lib;
import com.tty.lib.tool.Log;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import static com.tty.listener.player.PlayerActionListener.PLAYER_RIDE_ACTION_MAP;

public class PlayerRide extends BaseAction {

    public PlayerRide(Player player) {
        super(player);
    }

    @Override
    public boolean action(Entity passenger) {
        if (passenger instanceof Player p) {
            this.createEntity(this.player.getEyeLocation());
            this.createTask();
            return this.entity.addPassenger(p);
        }
        return false;
    }

    @Override
    public boolean check() {
        //如果之前存在
        if (PLAYER_RIDE_ACTION_MAP.containsKey(this.player)) return false;
        //被点击的玩家如果有乘客（隐藏实体
        return this.player.getPassengers().isEmpty();
    }

    @Override
    protected void createEntity(Location location) {
        this.entity = this.player.getWorld().spawnEntity(
                location,
                EntityType.AREA_EFFECT_CLOUD,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                e -> {
                    if (e instanceof AreaEffectCloud cloud) {
                        cloud.setInvulnerable(true);
                        cloud.setGravity(false);
                        cloud.setInvisible(true);
                        cloud.setRadius(0);
                        this.player.addPassenger(cloud);
                    }
                });
    }

    private void createTask() {
        this.task = Lib.Scheduler.runAtEntityFixedRate(
                Ari.instance,
                this.player,
                i -> {
                    Log.debug("player: " + this.player.getName() + " ride now, status: " + PLAYER_RIDE_ACTION_MAP.size());
                    if (this.entity.getPassengers().isEmpty() ||
                            this.player.isDead() ||
                            !this.player.isOnline() ||
                            this.player.isSneaking() ||
                            this.player.isSwimming()) {
                        this.cancel();
                        PLAYER_RIDE_ACTION_MAP.remove(this.player);
                    }
                },
                () -> {
                    this.cancel();
                    PLAYER_RIDE_ACTION_MAP.remove(this.player);
                },
                1L,
                20L);
    }
}
