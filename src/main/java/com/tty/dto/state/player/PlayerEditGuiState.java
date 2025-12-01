package com.tty.dto.state.player;

import com.tty.Ari;
import com.tty.dto.CustomInventoryHolder;
import com.tty.lib.dto.State;
import com.tty.lib.enum_type.FunctionType;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class PlayerEditGuiState extends State {

    @Getter
    private final CustomInventoryHolder holder;
    @Getter
    private final FunctionType functionType;

    public PlayerEditGuiState(Entity owner, CustomInventoryHolder holder, FunctionType functionType) {
        super(owner, Ari.instance.getConfig().getInt("server.gui-edit-timeout", 10));
        this.holder = holder;
        this.functionType = functionType;
    }
}
