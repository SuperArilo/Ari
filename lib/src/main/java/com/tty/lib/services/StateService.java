package com.tty.lib.services;

import com.tty.lib.Log;
import com.tty.lib.dto.State;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class StateService {

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

    public StateService(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        this.rate = rate;
        this.c = c;
        this.isAsync = isAsync;
        this.plugin = javaPlugin;
    }

    private CancellableTask createTask(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        if (isAsync) {
            return Lib.Scheduler.runAsyncAtFixedRate(javaPlugin, i -> this.execute(), c, rate);
        } else {
            return Lib.Scheduler.runAtFixedRate(javaPlugin, i -> this.execute(), c, rate);
        }
    }

    private void execute() {
        if (STATE_LIST.isEmpty()) {
            this.abort();
            return;
        }

        for (State state : new ArrayList<>(this.STATE_LIST)) {
            state.safeCountIncrement();
            if (state.isOverload()) {
                Log.error("state from player %s is overload. force removal. state class: %s", state.getOwner().getName(), state.getClass().getName());
                STATE_LIST.remove(state);
            }
            if (state.isOver()) {
                STATE_LIST.remove(state);
                this.onEarlyExit(state);
                continue;
            }

            if (state.isDone()) {
                STATE_LIST.remove(state);
                this.onFinished(state);
                continue;
            }

            if (!state.isPending()) {
                state.setPending(true);
                state.increment();
                state.setSafeCount(0);
                this.loopExecution(state);
            }
        }
    }

    public void abort() {
        if (this.task != null) {
            this.task.cancel();
            this.STATE_LIST.clear();
            this.task = null;
            Log.debug("state service abort.");
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
                Log.debug("create state service");
            }
        }
    }

    public boolean hasState(Entity owner) {
        synchronized (this.STATE_LIST) {
            return this.getStates(owner).isEmpty();
        }
    }

    public List<State> getStates(Entity owner) {
        synchronized (this.STATE_LIST) {
            return STATE_LIST.stream()
                    .filter(i -> i.getOwner().equals(owner))
                    .toList();
        }
    }

    public boolean removeState(State state) {
        synchronized (this.STATE_LIST) {
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
     * 在当前计数下的执行内容
     *
     * @param state 当前检查的状态
     */
    protected abstract void loopExecution(State state);

    /**
     * 终止当前的状态添加
     * @param state 添加的状态
     */
    protected abstract void abortAddState(State state);

    /**
     * 当前的状态可添加
     * @param state 添加成功的状态
     */
    protected abstract void passAddState(State state);

    /**
     * 提前结束检查的回调方法
     * @param state 检查失败的状态
     */
    protected abstract void onEarlyExit(State state);

    /**
     * 计数完成后正常结束
     * @param state 检查通过的状态
     */
    protected abstract void onFinished(State state);

}
