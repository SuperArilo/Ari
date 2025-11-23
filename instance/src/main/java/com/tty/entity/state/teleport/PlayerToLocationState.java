package com.tty.entity.state.teleport;

import com.tty.entity.state.State;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerToLocationState extends State {

    @Getter
    private final Location location;

    public PlayerToLocationState(Player owner, int max_count, Location location) {
        super(owner, max_count);
        this.location = location;
    }

}
