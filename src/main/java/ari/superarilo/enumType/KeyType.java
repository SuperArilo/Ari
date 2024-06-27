package ari.superarilo.enumType;

public enum KeyType {
    TPASENDER("[TpaSender]"),
    TPAHERESENDER("[TpaHereSender]"),
    TPABESENDER("[TpaBeSender]");
    private final String type;

    KeyType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
