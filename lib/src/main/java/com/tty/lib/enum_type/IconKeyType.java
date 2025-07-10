package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum IconKeyType {
    ID("[ID]"),
    X("[X]"),
    Y("[Y]"),
    Z("[Z]"),
    WORLDNAME("[worldName]"),
    PLAYERNAME("[playerName]"),
    COST("[cost]"),
    TOP_SLOT("[top_slot]"),
    PERMISSION("[permission]");

    private final String key;

    IconKeyType(String key) {
        this.key = key;
    }

}
