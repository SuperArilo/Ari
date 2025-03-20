package ari.superarilo.enumType;

import lombok.Getter;

@Getter
public enum LangType {
    TPASENDER("[TpaSender]"),
    TPAHERESENDER("[TpaHereSender]"),
    TPABESENDER("[TpaBeSender]"),
    COSTED("[costed]");
    private final String type;

    LangType(String type) {
        this.type = type;
    }

}
