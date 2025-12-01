package com.tty.dto.state.player;

import com.tty.dto.state.AsyncState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

public class PlayerSaveState extends AsyncState {

    @Getter
    @Setter
    private long loginTime = 0;

    public PlayerSaveState(Entity owner) {
        super(owner, Integer.MAX_VALUE);
    }

}
