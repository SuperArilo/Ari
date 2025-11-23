package com.tty.entity.state.teleport;

import com.tty.entity.state.State;
import lombok.Getter;
import org.bukkit.entity.Player;

public class PlayerToPlayerState extends State {

    @Getter
    private final Player target;
    @Getter
    private final String command;

    public PlayerToPlayerState(Player owner, Player target, int max_count, String command) {
        super(owner, max_count);
        this.target = target;
        this.command = command;
    }

}
