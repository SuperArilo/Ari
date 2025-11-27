package com.tty.states;

import com.tty.entity.state.State;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import com.tty.lib.tool.Log;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class StateMachine {

    private final JavaPlugin plugin;
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
    private CancellableTask task;

    @Getter
    private final List<State> STATE_LIST = Collections.synchronizedList(new ArrayList<>());

    public StateMachine(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        this.rate = rate;
        this.c = c;
        this.isAsync = isAsync;
        this.plugin = javaPlugin;
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
        synchronized (this.STATE_LIST) {
            if (STATE_LIST.isEmpty()) {
                this.abort();
                return;
            }
            List<State> toRemove = new ArrayList<>();
            for (State state : this.STATE_LIST) {
                if (!condition(state)) {
                    toRemove.add(state);
                    this.onEarlyExit(state);
                } else if (state.isDone()) {
                    toRemove.add(state);
                    this.onFinished(state);
                } else {
                    state.increment();
                }
            }
            this.STATE_LIST.removeAll(toRemove);
        }
    }


    public void abort() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
            Log.debug("state machine abort");
        }
    }

    public void addState(State state) {
        synchronized (this.STATE_LIST) {
            if (!this.canAddState(state)) {
                this.abortAddState(state);
                return;
            }
            this.STATE_LIST.add(state);
            this.passAddState(state);
            if (task == null) {
                this.task = createTask(rate, c, isAsync, this.plugin);
                Log.debug("create state machine");
            }
        }
    }

    public List<State> getStates(Entity owner) {
        synchronized (STATE_LIST) {
            return STATE_LIST.stream()
                    .filter(i -> i.getOwner().equals(owner))
                    .toList();
        }
    }

    public boolean removeState(State state) {
        synchronized (STATE_LIST) {
            return STATE_LIST.remove(state);
        }
    }
    /**
     * 检查是否允许添加状态
     * @param state 要添加的状态
     * @return true 表示允许添加，false 表示不允许
     */
    protected abstract boolean canAddState(State state);

    /**
     * 在给定计数下的条件检查
     * @param state 当前检查的状态
     * @return true 检查通过进入下一次检查，反之直接结束
     */
    public abstract boolean condition(State state);

    /**
     * 终止当前的状态添加
     * @param state 添加的状态
     */
    public abstract void abortAddState(State state);

    /**
     * 当前的状态可添加
     * @param state 添加成功的状态
     */
    public abstract void passAddState(State state);

    /**
     * 提前结束检查的回调方法
     * @param state 检查失败的状态
     */
    public abstract void onEarlyExit(State state);

    /**
     * 计数完成后正常结束
     * @param state 检查通过的状态
     */
    public abstract void onFinished(State state);

}
