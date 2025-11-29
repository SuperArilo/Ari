package com.tty.lib.dto;

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

    /**
     * 是否提前结束
     */
    private boolean isOver = false;

    /**
     * 当前的次数是否在进行中
     */
    private boolean pending = false;

    private int safeCount = 0;

    public State(Entity owner, int max_count) {
        this.owner = owner;
        this.max_count = max_count;
    }

    public void increment() {
        if (this.count < this.max_count) {
            this.count++;
        }
    }

    public boolean isDone() {
        return this.count >= this.max_count;
    }

    public void safeCountIncrement() {
        this.safeCount++;
    }

    public boolean isOverload() {
        return this.safeCount >= 5;
    }
}
