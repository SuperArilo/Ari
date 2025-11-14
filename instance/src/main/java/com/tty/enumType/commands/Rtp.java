package com.tty.enumType.commands;

import lombok.Getter;

@Getter
public enum Rtp {

    CANCEL("cancel");

    private final String name;

    Rtp(String name) {
        this.name = name;
    }
}
