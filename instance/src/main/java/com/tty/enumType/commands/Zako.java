package com.tty.enumType.commands;

import lombok.Getter;

@Getter
public enum Zako {

    ADD("add"),
    REMOVE("remove"),
    LIST("list"),
    INFO("info");

    private final String name;

    Zako(String name) {
        this.name = name;
    }
}
