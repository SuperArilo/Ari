package com.tty.lib.services;

import com.tty.lib.dto.State;
import org.bukkit.entity.Entity;

import java.util.List;

public interface StateService {
    void execute();
    void abort();
    void addState(State state);
    List<State> getStates(Entity owner);
    boolean hasState(Entity owner);
    boolean removeState(State state);
}
