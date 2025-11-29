package com.tty.tool;

import java.util.HashMap;
import java.util.Map;

import com.tty.lib.services.StateService;
import com.tty.states.*;
import com.tty.states.action.PlayerRideActionStateService;
import com.tty.states.action.PlayerSitActionStateService;
import com.tty.states.teleport.PreTeleportStateService;
import com.tty.states.teleport.RandomTpStateService;
import com.tty.states.teleport.TeleportStateService;
import org.bukkit.plugin.java.JavaPlugin;

public class StateMachineManager {

    private final JavaPlugin plugin;
    private final Map<Class<? extends StateService>, StateService> stateMachines = new HashMap<>();

    public StateMachineManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 注册状态机
     */
    public <T extends StateService> void registerStateMachine(T machine) {
        this.stateMachines.put(machine.getClass(), machine);
    }

    /**
     * 初始化默认状态机
     */
    public void initDefaultStateMachines() {
        this.registerStateMachine(new PreTeleportStateService(20L, 1L, true, this.plugin));
        this.registerStateMachine(new TeleportStateService(20L, 1L, true, this.plugin));
        this.registerStateMachine(new CoolDownStateService(20L, 1L, true, this.plugin));
        this.registerStateMachine(new RandomTpStateService(20L, 1L, true, this.plugin));
        this.registerStateMachine(new PlayerSitActionStateService(20L, 1L, false, this.plugin));
        this.registerStateMachine(new PlayerRideActionStateService(20L, 1L, false, this.plugin));
        this.registerStateMachine(new GuiEditStateService(20L,1L, true, this.plugin));
    }

    /**
     * 按类型获取状态机
     */
    @SuppressWarnings("unchecked")
    public <T extends StateService> T get(Class<T> clazz) {
        return (T) this.stateMachines.get(clazz);
    }

    /**
     * 遍历所有状态机执行一些操作（可选）
     */
    public void forEach(java.util.function.Consumer<StateService> action) {
        this.stateMachines.values().forEach(action);
    }
}