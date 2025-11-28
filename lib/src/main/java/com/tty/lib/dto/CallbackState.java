package com.tty.lib.dto;

import org.bukkit.entity.Entity;

public class CallbackState extends State {

    private final StateCondition customCondition;
    private final Runnable successCallback;

    public CallbackState(Entity owner, int max_count, StateCondition customCondition, Runnable successCallback) {
        super(owner, max_count);
        this.customCondition = customCondition;
        this.successCallback = successCallback;
    }

    public boolean checkCondition() {
        return customCondition == null || customCondition.test();
    }

    public void executeCallback() {
        if (successCallback != null) {
            successCallback.run();
        }
    }
}
