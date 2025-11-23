package com.tty.states;

import com.tty.entity.state.State;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.Log;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class StateMachine {

    /**
     * 每一次的执行周期 单位tick
     */
    @Getter
    private final long rate;
    /**
     * 延迟多久后开始执行
     */
    @Getter
    private final long c;
    /**
     * 是异步还是同步
     */
    @Getter
    private final boolean isAsync;
    private final CancellableTask task;

    private final List<State> STATE_LIST = Collections.synchronizedList(new ArrayList<>());

    public StateMachine(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        this.rate = rate;
        this.c = c;
        this.isAsync = isAsync;

        this.task = this.createTask(rate, c, isAsync, javaPlugin);
    }

    private CancellableTask createTask(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        if (isAsync) {
            return Lib.Scheduler.runAsyncAtFixedRate(javaPlugin, this::execute, c, rate);
        } else {
            return Lib.Scheduler.runAtFixedRate(javaPlugin, this::execute, c, rate);
        }
    }

    private void execute(CancellableTask task) {
        if (STATE_LIST.isEmpty()) return;

        Iterator<State> iterator = this.STATE_LIST.iterator();
        Log.debug("STATE_LIST: " + this.STATE_LIST.size());
        while (iterator.hasNext()) {
            State state = iterator.next();
            if (!condition(state)) {
                iterator.remove();
                onFail(state);
            } else {
                state.increment();
                if (state.isDone()) {
                    iterator.remove();
                    onSuccess(state);
                }
            }
        }
    }

    public void abort() {
        if (this.task != null) {
            this.task.cancel();
        }
    }

    public void addState(State state) {
        this.STATE_LIST.add(state);
    }

    public List<State> getStates(Player owner) {
        return this.STATE_LIST.stream().filter(i -> i.getOwner().equals(owner)).toList();
    }

    public boolean removeState(State state) {
        return this.STATE_LIST.remove(state);
    }

    public abstract boolean condition(State state);

    public abstract void onFail(State state);

    public abstract void onSuccess(State state);

}
