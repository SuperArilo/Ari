package ari.superarilo.enumType;

public enum TeleportObjectType {
    TPASENDER("[TpaSender]"),
    TPAHERESENDER("[TpaHereSender]"),
    TPABESENDER("[TpaBeSender]");
    private final String type;

    TeleportObjectType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
