package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum CommandAction {

    ADD("add"),
    REMOVE("remove");

    private final String name;

    CommandAction(String name) {
        this.name = name;
    }
}
