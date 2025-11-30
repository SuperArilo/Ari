package com.tty.lib.dto;

import com.tty.lib.enum_type.TeleportType;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class TeleportState extends State {

    @Getter
    private final TeleportType type;

    public TeleportState(Entity owner, TeleportType type, int max_count) {
        super(owner, max_count);
        this.type = type;
    }
}
