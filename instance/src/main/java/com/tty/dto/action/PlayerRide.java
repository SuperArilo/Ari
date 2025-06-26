package com.tty.dto.action;

import com.tty.Ari;
import com.tty.dto.BaseAction;
import com.tty.lib.Lib;
import com.tty.lib.tool.Log;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;


public class PlayerRide extends BaseAction {

    private final Player bePlayer;

    public PlayerRide(Player actionPlayer, Player bePlayer) {
        super(actionPlayer);
        this.bePlayer = bePlayer;
    }

    @Override
    public void action() {
        this.createEntity(this.bePlayer.getEyeLocation());
        this.createTask();
        this.entity.addPassenger(this.action_player);
        this.add();
    }

    @Override
    public boolean check() {
        //如果之前存在
        if (PLAYER_ACTION_MAP.containsKey(this.action_player)) return false;
        //被点击的玩家如果有乘客（隐藏实体
        return this.bePlayer.getPassengers().isEmpty();
    }

    @Override
    protected void createEntity(Location location) {
        this.entity = this.bePlayer.getWorld().spawnEntity(
                location,
                EntityType.AREA_EFFECT_CLOUD,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                e -> {
                    if (e instanceof AreaEffectCloud cloud) {
                        cloud.setInvulnerable(true);
                        cloud.setGravity(false);
                        cloud.setInvisible(true);
                        cloud.setRadius(0);
                        this.bePlayer.addPassenger(cloud);
                    }
                });
    }

    private void createTask() {
        this.task = Lib.Scheduler.runAtEntityFixedRate(
                Ari.instance,
                this.bePlayer,
                i -> {
                    Log.debug("action_player: " + this.action_player.getName() + " ride " + this.bePlayer.getName() + " now, status: " + PLAYER_ACTION_MAP.size());
                    if (!this.entity.isInsideVehicle() ||
                            this.entity.getPassengers().isEmpty() ||
                            this.bePlayer.isDead() ||
                            !this.bePlayer.isOnline() ||
                            this.bePlayer.isSneaking() ||
                            this.bePlayer.isSwimming()) {
                        this.cancel();
                    }
                },
                this::cancel,
                1L,
                20L);
    }
}
