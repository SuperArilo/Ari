package com.tty.states.action;

import com.tty.Ari;
import com.tty.lib.Log;
import com.tty.lib.dto.State;
import com.tty.dto.state.action.PlayerRideActionState;
import com.tty.lib.Lib;
import com.tty.lib.services.impl.StateServiceImpl;
import lombok.SneakyThrows;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;



public class PlayerRideActionStateServiceImpl extends StateServiceImpl {

    public PlayerRideActionStateServiceImpl(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    protected boolean canAddState(State state) {
        if (!(state instanceof PlayerRideActionState s)) return false;

        Player owner = (Player) s.getOwner();
        String playerName = owner.getName();
        //判断玩家是否已经 ride 了
        if (!this.getStates(owner).isEmpty()) {
            Log.debug("player %s is sited. skip...", playerName);
            return false;
        }
        //被点击的玩家如果有乘客（隐藏实体
        return s.getBeRidePlayer().getPassengers().isEmpty();

    }

    @SneakyThrows
    @Override
    protected void condition(State state) {
        if (!(state instanceof PlayerRideActionState s)) return;
        Player beRidePlayer = s.getBeRidePlayer();
        Player owner = (Player) s.getOwner();
        Entity toolEntity = s.getTool_entity();
        boolean b = toolEntity.getPassengers().isEmpty() ||
                !toolEntity.isInsideVehicle() ||
                beRidePlayer.isDead() ||
                owner.isSleeping() ||
                owner.isSneaking() ||
                owner.isDeeplySleeping() ||
                !beRidePlayer.isOnline() ||
                beRidePlayer.isSneaking() ||
                beRidePlayer.isSwimming();
        if (b) {
            state.setOver(true);
        }
    }

    @Override
    protected void abortAddState(State state) {

    }

    @Override
    protected void passAddState(State state) {
        if (!(state instanceof PlayerRideActionState s)) return;
        Player beRidePlayer = s.getBeRidePlayer();
        Entity entity = beRidePlayer.getWorld().spawnEntity(
                beRidePlayer.getEyeLocation(),
                EntityType.AREA_EFFECT_CLOUD,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                e -> {
                    if (e instanceof AreaEffectCloud cloud) {
                        cloud.setRadius(0);
                        cloud.setInvulnerable(true);
                        cloud.setGravity(false);
                        cloud.setInvisible(true);
                        beRidePlayer.addPassenger(cloud);
                    }
                });
        s.setTool_entity(entity);
        entity.addPassenger(s.getOwner());
    }

    @Override
    protected void onEarlyExit(State state) {
        if (!(state instanceof PlayerRideActionState s)) return;
        Player owner = (Player) s.getOwner();
        Entity toolEntity = s.getTool_entity();
        owner.eject();
        Log.debug("player %s eject to player %s, remove tool entity", owner.getName(), s.getBeRidePlayer().getName());
        if (toolEntity != null) {
            Lib.Scheduler.runAtEntity(
                    Ari.instance,
                    toolEntity,
                    i -> {
                        toolEntity.remove();
                        s.setTool_entity(null);
                    },
                    () -> Log.error("error on player %s sit when remove tool entity", owner.getName()));
        }
    }

    @Override
    protected void onFinished(State state) {

    }
}
