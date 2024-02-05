package ari.superarilo.enumType;

public enum KeyType {
    TPASENDER("[TpaSender]"),
    TPABESENDER("[TpaBeSender]");
    private final String type;

    KeyType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
