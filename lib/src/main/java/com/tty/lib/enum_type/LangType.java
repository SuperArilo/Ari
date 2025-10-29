package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum LangType {
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
    PLAYERNAME("[PlayerName]");

    private final String type;

    LangType(String type) {
        this.type = type;
    }

}
