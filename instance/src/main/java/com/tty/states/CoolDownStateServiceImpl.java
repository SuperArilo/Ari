package com.tty.states;

import com.tty.lib.dto.State;
import com.tty.lib.Log;
import com.tty.dto.state.teleport.CooldownState;
import com.tty.lib.services.impl.StateServiceImpl;
import com.tty.lib.tool.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class CoolDownStateServiceImpl extends StateServiceImpl {

    public CoolDownStateServiceImpl(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    protected boolean canAddState(State state) {
        return this.getStates(state.getOwner()).isEmpty();
    }

    @Override
    protected void condition(State state) {
        if (state instanceof CooldownState s && state.getOwner() instanceof Player p && !p.isOp()) {
            if (PermissionUtils.hasPermission(p, "ari.cooldown." + s.getType().getKey())) {
                state.setOver(true);
            }
            Log.debug("entity %s teleport cd time is cooling down", state.getOwner().getName());
            return;
        }
        state.setOver(true);
    }

    @Override
    protected void abortAddState(State state) {

    }

    @Override
    protected void passAddState(State state) {

    }

    @Override
    protected void onEarlyExit(State state) {
        Log.debug("entity %s cd time has ended.", state.getOwner().getName());
    }

    @Override
    protected void onFinished(State state) {
        if (state instanceof CooldownState cdState) {
            Log.debug("entity %s cd time has ended.", cdState.getOwner().getName());
        }
    }
}
