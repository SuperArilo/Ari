package com.tty.entity.state;

import lombok.Data;
import org.bukkit.entity.Entity;

@Data
public class State {

    private Entity owner;
    /**
     * 基础计数
     */
    private int count = 0;
    /**
     * 最大检查计数
     */
    private int max_count;

    private boolean isOver = false;

    public State(Entity owner, int max_count) {
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
