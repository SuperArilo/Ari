package com.tty.lib.enum_type;

public enum PeriodicTaskEnum implements NameTypeEnum {

    PLAYER_SAVE("player_save");

    private final String name;

    PeriodicTaskEnum(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
