package com.tty.entity.state.teleport;

import com.tty.entity.state.CallbackState;
import com.tty.entity.state.StateCondition;
import com.tty.enumType.TeleportType;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class EntityToLocationCallbackState extends CallbackState {

    @Getter
    private final Location location;
    @Getter
    private final TeleportType type;

    public EntityToLocationCallbackState(Entity owner, int max_count, Location location, StateCondition customCondition, Runnable successCallback, TeleportType type) {
        super(owner, max_count, customCondition, successCallback);
        this.location = location;
        this.type = type;
    }
}
