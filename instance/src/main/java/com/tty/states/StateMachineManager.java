package com.tty.states;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class StateMachineManager {

    private final JavaPlugin plugin;
    private final Map<Class<? extends StateMachine>, StateMachine> stateMachines = new HashMap<>();

    public StateMachineManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 注册状态机
     */
    public <T extends StateMachine> void registerStateMachine(T machine) {
        this.stateMachines.put(machine.getClass(), machine);
    }

    /**
     * 初始化默认状态机
     */
    public void initDefaultStateMachines() {
        this.registerStateMachine(new PreTeleportStateMachine(20L, 1L, true, this.plugin));
        this.registerStateMachine(new TeleportStateMachine(20L, 1L, true, this.plugin));
        this.registerStateMachine(new CoolDownStateMachine(20L, 1L, true, this.plugin));
        this.registerStateMachine(new RandomTpStateMachine(20L, 1L, true, this.plugin));
    }

    /**
     * 按类型获取状态机
     */
    @SuppressWarnings("unchecked")
    public <T extends StateMachine> T get(Class<T> clazz) {
        return (T) this.stateMachines.get(clazz);
    }

    /**
     * 遍历所有状态机执行一些操作（可选）
     */
    public void forEach(java.util.function.Consumer<StateMachine> action) {
        this.stateMachines.values().forEach(action);
    }
}