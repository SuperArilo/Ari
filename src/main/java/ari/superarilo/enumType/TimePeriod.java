package ari.superarilo.enumType;

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
    WAKEUP(23460,23460, "wakeUp");
    private final long start;
    private final long end;
    private final String description;

    TimePeriod(long start, long end, String description) {
        this.start = start;
        this.end = end;
        this.description = description;
    }
}
