package com.tty.states;

import com.tty.Ari;
import com.tty.entity.state.State;
import com.tty.entity.state.teleport.PlayerToPlayerState;
import com.tty.enumType.FilePath;
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

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class TeleportingStateMachine extends StateMachine {

    private final Map<Entity, Double> initHealthMap = new WeakHashMap<>();
    private final Map<Entity, Location> initLocationMap = new WeakHashMap<>();

    public TeleportingStateMachine(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    public boolean condition(State state) {
        Entity owner = state.getOwner();

        List<State> preTpList = Ari.instance.preTeleportStateMachine.getStates(owner);
        if (!preTpList.isEmpty()) {
            for (State s : preTpList) {
                Ari.instance.preTeleportStateMachine.removeState(s);
            }

        }

        if (!this.initLocationMap.containsKey(owner))  {
            this.initLocationMap.put(owner, owner.getLocation().clone());
        }
        if (owner instanceof Damageable damageable && !this.initHealthMap.containsKey(owner)) {
            this.initHealthMap.put(owner, damageable.getHealth());
        }

        //判断玩家是否在传送过程中移动或者受伤
        boolean b = (!(owner instanceof Player player) || player.isOnline()) &&
                (!(owner instanceof Damageable damageable) || (!this.hasMoved(owner) && !this.hasLostHealth(damageable)));
        if (b) {
            owner.showTitle(ComponentUtils.setPlayerTitle(
                    Ari.C_INSTANCE.getValue("teleport.title.main", FilePath.Lang),
                    Ari.C_INSTANCE.getValue("teleport.title.sub-title", FilePath.Lang).replace(LangType.TELEPORTDELAY.getType(), String.valueOf(state.getMax_count() - state.getCount())),
                    200,
                    1000,
                    200));
        }
        return b;
    }

    @Override
    public void onFail(State state) {
        Entity owner = state.getOwner();
        owner.sendMessage(ConfigUtils.t("teleport.break"));
        this.initHealthMap.remove(owner);
        this.initLocationMap.remove(owner);
        Log.debug("TeleportingStateMachine: initHealthMap: " + this.initHealthMap.size());
        Log.debug("TeleportingStateMachine: initHealthMap: " + this.initLocationMap.size());
    }

    @Override
    public void onSuccess(State state) {
        if (state instanceof PlayerToPlayerState toPlayerState) {
            Teleporting.create(toPlayerState.getOwner(), toPlayerState.getTarget().getLocation()).teleport();
        }

        Log.debug("success");
    }

    /**
     * 检查是否受伤
     * @param entity 被检查的实体
     */
    private boolean hasLostHealth(Damageable entity) {
        return entity.getHealth() < this.initHealthMap.get(entity);
    }

    /**
     * 检查是否移动
     * @param entity 被检查的实体
     */
    protected boolean hasMoved(Entity entity) {
        Location currentLocation = entity.getLocation();
        Location initLocation = this.initLocationMap.get(entity);
        return makePositive(initLocation.getX() - currentLocation.getX()) + makePositive(initLocation.getY() - currentLocation.getY()) + makePositive(initLocation.getZ() - currentLocation.getZ()) > 0.1;
    }

    protected double makePositive(double d) {
        if (d < 0) {
            d = d * -1D;
        }
        return d;
    }
}
