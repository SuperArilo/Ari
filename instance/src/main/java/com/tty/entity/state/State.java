package com.tty.entity.state;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class State {

    private Player owner;
    /**
     * 基础计数
     */
    private int count = 0;
    /**
     * 最大检查计数
     */
    private int max_count;

    public State(Player owner, int max_count) {
        this.owner = owner;
        this.max_count = max_count;
    }

    public void increment() {
        if (count < max_count) {
            count++;
        }
    }

    public boolean isDone() {
        return count >= max_count;
    }

}
