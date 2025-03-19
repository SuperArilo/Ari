package ari.superarilo.enumType;

import lombok.Getter;

@Getter
public enum TeleportObjectType {
    TPASENDER("[TpaSender]"),
    TPAHERESENDER("[TpaHereSender]"),
    TPABESENDER("[TpaBeSender]");
    private final String type;

    TeleportObjectType(String type) {
        this.type = type;
    }

}
