package ari.superarilo.enumType;

import lombok.Getter;

@Getter
public enum TimePeriod {

    SUNRISE(0, 1000, "sunrise", true),
    DAY(1000, 12000, "day", false),
    NOON(6000, 6001, "noon", false),
    SUNSET(12000, 13000, "sunset", true),
    NIGHT(13000, 23000, "night", false),
    MIDNIGHT(18000, 18001, "midnight", false),
    DAYLIGHT(23000, 24000, "daylight", false),
    WAKEUP(23460,23460, "wake", true);
    private final long start;
    private final long end;
    private final String description;
    private final boolean isServerUse;

    TimePeriod(long start, long end, String description, boolean isServerUse) {
        this.start = start;
        this.end = end;
        this.description = description;
        this.isServerUse = isServerUse;
    }
}
