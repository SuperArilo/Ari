package com.tty.entity.state.teleport;

import com.tty.lib.dto.State;
import com.tty.enumType.TeleportType;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class CooldownState extends State {

    @Getter
    private final TeleportType type;

    public CooldownState(Entity owner, int max_count, TeleportType type) {
        super(owner, max_count);
        this.type = type;
    }
}
