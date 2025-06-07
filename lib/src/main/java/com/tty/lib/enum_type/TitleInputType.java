package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum TitleInputType {
    RENAME("rename"),
    PERMISSION("permission"),
    COST("cost");

    private final String name;

    TitleInputType(String name) {
        this.name = name;
    }

}
