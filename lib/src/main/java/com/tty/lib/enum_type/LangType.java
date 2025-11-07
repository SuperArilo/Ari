package com.tty.lib.enum_type;

import java.util.HashMap;
import java.util.Map;

public enum LangType implements LangTypeEnum {
    TPASENDER("[TpaSender]"),
    TPAHERESENDER("[TpaHereSender]"),
    TPABESENDER("[TpaBeSender]"),
    COSTED("[Costed]"),
    TIME("[Time]"),
    DEATHLOCATION("[DeathLocation]"),
    PERIOD("[Period]"),
    SLEEPPLAYERS("[SleepPlayers]"),
    SKIPNIGHTTICKINCREMENT("[SkipNightTickIncrement]"),
    SPAWNLOCATION("[SpawnLocation]"),
    SOURCEDISPLAYNAME("[SourceDisplayNane]"),
    CHATMESSAGE("[Message]"),
    RTPSEARCHCOUNT("[RtpSearchCount]"),
    TELEPORTDELAY("[TeleportDelay]"),
    PLAYERNAME("[PlayerName]"),
    FIRSTLOGINSERVERTIME("[FirstLoginServerTime]"),
    LASTLOGINSERVERTIME("[LastLoginServerTime]"),
    TOTALONSERVER("[TotalOnServer]"),
    PLAYERWORLD("[PlayerWorld]"),
    PLAYERLOCATION("[PlayerLocation]"),
    KILLER("[Killer]"),
    VICTIM("[Victim]"),
    KILLER_ITEM("[Killer_Item]");

    private final String type;
    private static final Map<String, LangType> TYPE_MAP = new HashMap<>();

    static {
        for (LangType lt : values()) {
            TYPE_MAP.put(lt.getType(), lt);
        }
    }

    LangType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    public static LangType fromType(String type) {
        return TYPE_MAP.get(type);
    }
}
