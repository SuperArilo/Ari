package com.tty.lib.enum_type;

import lombok.Getter;

@Getter
public enum TimePeriod {

    SUNRISE(0, 1000, "sunrise"),
    DAY(1000, 12000, "day"),
    NOON(6000, 6001, "noon"),
    SUNSET(12000, 13000, "sunset"),
    NIGHT(13000, 23000, "night"),
    MIDNIGHT(18000, 18001, "midnight"),
    DAYLIGHT(23000, 24000, "daylight"),
    WAKE_UP(23460,23460, "wake_up");
    private final long start;
    private final long end;
    private final String description;

    TimePeriod(long start, long end, String description) {
        this.start = start;
        this.end = end;
        this.description = description;
    }
}
