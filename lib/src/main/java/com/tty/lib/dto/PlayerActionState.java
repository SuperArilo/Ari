package com.tty.lib.dto;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

public class PlayerActionState extends State {

    @Getter
    @Setter
    private Entity tool_entity;

    public PlayerActionState(Entity owner) {
        super(owner, Integer.MAX_VALUE);
    }

}
