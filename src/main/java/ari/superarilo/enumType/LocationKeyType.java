package ari.superarilo.enumType;

public enum LocationKeyType {
    ID("[ID]"),
    X("[X]"),
    Y("[Y]"),
    Z("[Z]"),
    WORLDNAME("[worldName]"),
    PLAYERNAME("[playerName]");

    private final String key;

    LocationKeyType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
