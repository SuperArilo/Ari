package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum LangType {
    TPASENDER("[TpaSender]"),
    TPAHERESENDER("[TpaHereSender]"),
    TPABESENDER("[TpaBeSender]"),
    COSTED("[costed]"),
    TIME("[time]"),
    DEATHLOCATION("[DeathLocation]"),
    PERIOD("[period]");
    private final String type;

    LangType(String type) {
        this.type = type;
    }

}
