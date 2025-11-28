package com.tty.tool;

import java.util.HashMap;
import java.util.Map;

import com.tty.lib.services.impl.StateServiceImpl;
import com.tty.states.*;
import com.tty.states.action.PlayerRideActionStateServiceImpl;
import com.tty.states.action.PlayerSitActionStateServiceImpl;
import com.tty.states.teleport.PreTeleportStateServiceImpl;
import com.tty.states.teleport.RandomTpStateServiceImpl;
import com.tty.states.teleport.TeleportStateServiceImpl;
import org.bukkit.plugin.java.JavaPlugin;

public class StateMachineManager {

    private final JavaPlugin plugin;
    private final Map<Class<? extends StateServiceImpl>, StateServiceImpl> stateMachines = new HashMap<>();

    public StateMachineManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 注册状态机
     */
    public <T extends StateServiceImpl> void registerStateMachine(T machine) {
        this.stateMachines.put(machine.getClass(), machine);
    }

    /**
     * 初始化默认状态机
     */
    public void initDefaultStateMachines() {
        this.registerStateMachine(new PreTeleportStateServiceImpl(20L, 1L, true, this.plugin));
        this.registerStateMachine(new TeleportStateServiceImpl(20L, 1L, true, this.plugin));
        this.registerStateMachine(new CoolDownStateServiceImpl(20L, 1L, true, this.plugin));
        this.registerStateMachine(new RandomTpStateServiceImpl(20L, 1L, true, this.plugin));
        this.registerStateMachine(new PlayerSitActionStateServiceImpl(20L, 1L, false, this.plugin));
        this.registerStateMachine(new PlayerRideActionStateServiceImpl(20L, 1L, false, this.plugin));
        this.registerStateMachine(new GuiEditStateServiceImpl(20L,1L, true, this.plugin));
    }

    /**
     * 按类型获取状态机
     */
    @SuppressWarnings("unchecked")
    public <T extends StateServiceImpl> T get(Class<T> clazz) {
        return (T) this.stateMachines.get(clazz);
    }

    /**
     * 遍历所有状态机执行一些操作（可选）
     */
    public void forEach(java.util.function.Consumer<StateServiceImpl> action) {
        this.stateMachines.values().forEach(action);
    }
}