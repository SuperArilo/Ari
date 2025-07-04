package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum LangType {
    TPASENDER("[TpaSender]"),
    TPAHERESENDER("[TpaHereSender]"),
    TPABESENDER("[TpaBeSender]"),
    COSTED("[costed]"),
    TIME("[time]"),
    DEATHLOCATION("[DeathLocation]"),
    PERIOD("[period]"),
    SLEEPPLAYERS("[SleepPlayers]"),
    SKIPNIGHTTICKINCREMENT("[SkipNightTickIncrement]"),
    SPAWNLOCATION("[SpawnLocation]"),
    SOURCEDISPLAYNAME("[SourceDisplayNane]"),
    CHATMESSAGE("[Message]"),
    RTPSEARCHCOUNT("[RtpSearchCount]"),
    TELEPORTDELAY("[TeleportDelay]");
    private final String type;

    LangType(String type) {
        this.type = type;
    }

}
