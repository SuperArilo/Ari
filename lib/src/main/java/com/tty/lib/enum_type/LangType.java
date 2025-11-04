package com.tty.lib.enum_type;

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
    PLAYERLOCATION("[PlayerLocation]");

    private final String type;

    LangType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
