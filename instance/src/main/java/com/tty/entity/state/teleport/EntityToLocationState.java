package com.tty.entity.state.teleport;

import com.tty.lib.dto.State;
import com.tty.enumType.TeleportType;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityToLocationState extends State {

    @Getter
    private final Location location;
    @Getter
    private final TeleportType type;

    public EntityToLocationState(Entity owner, int max_count, Location location, TeleportType type) {
        super(owner, (owner instanceof Player p && p.isOp()) ? 0:max_count);
        this.location = location;
        this.type = type;
    }

}
