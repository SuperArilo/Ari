package com.tty.states;

import com.tty.Ari;
import com.tty.entity.state.State;
import com.tty.entity.state.teleport.CooldownState;
import com.tty.entity.state.teleport.EntityToEntityState;
import com.tty.entity.state.teleport.EntityToLocationCallbackState;
import com.tty.entity.state.teleport.EntityToLocationState;
import com.tty.enumType.FilePath;
import com.tty.enumType.TeleportType;
import com.tty.function.Teleporting;
import com.tty.lib.enum_type.LangType;
import com.tty.lib.tool.ComponentUtils;
import com.tty.lib.tool.Log;
import com.tty.tool.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TeleportStateMachine extends StateMachine {

    private final Map<UUID, Double> initHealthMap = new HashMap<>();
    private final Map<UUID, Location> initLocationMap = new HashMap<>();

    public TeleportStateMachine(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    public boolean condition(State state) {
        Entity owner = state.getOwner();
        this.addEntityInitData(owner);

        if (owner instanceof Player player && !player.isOnline()) {
            return false;
        }

        if (state instanceof EntityToEntityState entityToEntityState) {
            Entity target = entityToEntityState.getTarget();
            if (target instanceof Player targetPlayer && !targetPlayer.isOnline()) {
                owner.sendMessage(ConfigUtils.t("teleport.break"));
                return false;
            }
        }

        if (owner instanceof Damageable damageable) {
            if (this.hasMoved(owner) || this.hasLostHealth(damageable)) {
                owner.sendMessage(ConfigUtils.t("teleport.break"));
                return false;
            }
        }

        owner.showTitle(ComponentUtils.setPlayerTitle(
                Ari.C_INSTANCE.getValue("teleport.title.main", FilePath.Lang),
                Ari.C_INSTANCE.getValue("teleport.title.sub-title", FilePath.Lang)
                        .replace(LangType.TELEPORTDELAY.getType(), String.valueOf(state.getMax_count() - state.getCount())),
                200,
                1000,
                200
        ));
        Log.debug("checking entity " + owner.getName() + " teleporting");
        return true;
    }


    @Override
    protected boolean canAddState(State state) {
        Entity owner = state.getOwner();
        //判断当前实体是否在传送冷却中
        if (!Ari.instance.stateMachineManager.get(CoolDownStateMachine.class).getStates(owner).isEmpty()) {
            owner.sendMessage(ConfigUtils.t("teleport.cooling"));
            return false;
        }

        if(!Ari.instance.stateMachineManager.get(TeleportStateMachine.class).getStates(owner).isEmpty()) {
            owner.sendMessage(ConfigUtils.t("teleport.has-teleport"));
            return false;
        }

        if (state instanceof EntityToLocationCallbackState callbackState) {
            return callbackState.checkCondition();
        }

        return true;
    }

    @Override
    public void abortAddState(State state) {

    }

    @Override
    public void passAddState(State state) {

    }

    @Override
    public void onEarlyExit(State state) {
        Entity owner = state.getOwner();
        this.removeEntityInitData(owner);
    }

    @Override
    public void onFinished(State state) {
        Entity owner = state.getOwner();
        owner.clearTitle();
        CoolDownStateMachine machine = Ari.instance.stateMachineManager.get(CoolDownStateMachine.class);

        Location targetLocation;
        String targetName;
        Runnable afterAction;

        switch (state) {
            case EntityToEntityState toEntityState -> {
                targetLocation = toEntityState.getTarget().getLocation();
                targetName = toEntityState.getTarget().getName();
                afterAction = () -> handleTeleportAfter(owner, targetName,
                        () -> this.removeEntityInitData(owner),
                        () -> machine.addState(new CooldownState(owner, TeleportType.getCoolDownTime(toEntityState.getType()), toEntityState.getType()))
                );
            }
            case EntityToLocationState toLocationState -> {
                targetLocation = toLocationState.getLocation();
                if (targetLocation == null) return;
                targetName = targetLocation.toString();
                afterAction = () -> handleTeleportAfter(owner, targetName,
                        () -> this.removeEntityInitData(owner),
                        () -> machine.addState(new CooldownState(owner, TeleportType.getCoolDownTime(toLocationState.getType()), toLocationState.getType()))
                );
            }
            case EntityToLocationCallbackState callbackState -> {
                targetLocation = callbackState.getLocation();
                targetName = targetLocation.toString();
                afterAction = () -> {
                    callbackState.executeCallback();
                    handleTeleportAfter(owner, targetName,
                            () -> this.removeEntityInitData(owner),
                            () -> machine.addState(new CooldownState(owner, TeleportType.getCoolDownTime(callbackState.getType()), callbackState.getType()))
                    );
                };
            }
            default -> {
                return;
            }
        }
        Teleporting.create(owner, targetLocation)
                .teleport()
                .after(afterAction);
    }

    /**
     * 检查是否受伤
     */
    private boolean hasLostHealth(Damageable entity) {
        Double initHealth = this.initHealthMap.get(entity.getUniqueId());
        if (initHealth == null) return false;
        return entity.getHealth() < initHealth;
    }

    /**
     * 检查是否移动
     */
    protected boolean hasMoved(Entity entity) {
        Location initLocation = this.initLocationMap.get(entity.getUniqueId());
        if (initLocation == null) return false;
        return entity.getLocation().distanceSquared(initLocation) > 0.1;
    }

    private void addEntityInitData(Entity entity) {
        if (!this.initLocationMap.containsKey(entity.getUniqueId()))  {
            this.initLocationMap.put(entity.getUniqueId(), entity.getLocation().clone());
        }
        if (entity instanceof Damageable damageable && !this.initHealthMap.containsKey(entity.getUniqueId())) {
            this.initHealthMap.put(entity.getUniqueId(), damageable.getHealth());
        }
    }

    private void removeEntityInitData(Entity entity) {
        this.initHealthMap.remove(entity.getUniqueId());
        this.initLocationMap.remove(entity.getUniqueId());
    }

    private void handleTeleportAfter(Entity owner, String targetDesc, Runnable removeInit, Runnable addState) {
        removeInit.run();
        addState.run();
        Log.debug(String.format("Entity %s teleport to %s success", owner.getName(), targetDesc));
    }

}
