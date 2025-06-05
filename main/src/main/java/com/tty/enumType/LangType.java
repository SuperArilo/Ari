package com.tty.enumType;

import lombok.Getter;

@Getter
public enum LangType {
    TPASENDER("[TpaSender]"),
    TPAHERESENDER("[TpaHereSender]"),
    TPABESENDER("[TpaBeSender]"),
    COSTED("[costed]"),
    TIME("[time]"),
    PERIOD("[period]");
    private final String type;

    LangType(String type) {
        this.type = type;
    }

}
