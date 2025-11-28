package com.tty.states;

import com.tty.entity.state.State;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerActionStateMachine extends StateMachine {

    public PlayerActionStateMachine(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        super(rate, c, isAsync, javaPlugin);
    }

    @Override
    protected boolean canAddState(State state) {
        return false;
    }

    @Override
    protected void condition(State state) {
    }

    @Override
    protected void abortAddState(State state) {

    }

    @Override
    protected void passAddState(State state) {

    }

    @Override
    protected void onEarlyExit(State state) {

    }

    @Override
    protected void onFinished(State state) {

    }
}
