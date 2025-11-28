package com.tty.entity.state.teleport;

import com.tty.lib.dto.State;
import com.tty.enumType.TeleportType;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityToEntityState extends State {

    @Getter
    private final Entity target;
    @Getter
    private final String command;
    @Getter
    private final TeleportType type = TeleportType.TPA;

    public EntityToEntityState(Entity owner, Entity target, int max_count, String command) {
        super(owner, (owner instanceof Player p && p.isOp()) ? 0:max_count);
        this.target = target;
        this.command = command;
    }

}
