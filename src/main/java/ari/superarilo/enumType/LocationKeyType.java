package ari.superarilo.enumType;

import lombok.Getter;

@Getter
public enum LocationKeyType {
    ID("[ID]"),
    X("[X]"),
    Y("[Y]"),
    Z("[Z]"),
    WORLDNAME("[worldName]"),
    PLAYERNAME("[playerName]"),
    COST("[cost]"),
    PERMISSION("[permission]");

    private final String key;

    LocationKeyType(String key) {
        this.key = key;
    }

}
