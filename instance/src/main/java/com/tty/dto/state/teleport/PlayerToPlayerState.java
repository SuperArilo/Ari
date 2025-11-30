package com.tty.dto.state.teleport;

import com.tty.lib.enum_type.TeleportType;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class PlayerToPlayerState extends EntityToEntityState {

    @Getter
    private final String command;

    public PlayerToPlayerState(Entity owner, Entity target, int max_count, String command) {
        super(owner, TeleportType.TPA, target, max_count);
        this.command = command;
    }
}
