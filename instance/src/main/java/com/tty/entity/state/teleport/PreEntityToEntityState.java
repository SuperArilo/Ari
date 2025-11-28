package com.tty.entity.state.teleport;

import com.tty.lib.dto.State;
import lombok.Getter;
import org.bukkit.entity.Entity;

public class PreEntityToEntityState extends State {

    @Getter
    private final Entity target;
    @Getter
    private final String command;

    public PreEntityToEntityState(Entity owner, Entity target, int max_count, String command) {
        super(owner, max_count);
        this.target = target;
        this.command = command;
    }
}
