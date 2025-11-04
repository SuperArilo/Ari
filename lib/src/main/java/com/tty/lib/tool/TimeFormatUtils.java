package com.tty.lib.tool;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 计算经过的时间
 */
public class TimeFormatUtils {

    /**
     * 格式化经过的时间
     * @param milliseconds 毫秒数
     * @return 格式化的时间字符串
     */
    public static String format(long milliseconds) {
        return format(milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 格式化经过的时间
     * @param duration 时间量
     * @param unit 时间单位
     * @return 格式化的时间字符串
     */
    public static String format(long duration, TimeUnit unit) {
        long millis = unit.toMillis(duration);

        if (millis == 0) {
            return "0秒";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

        return buildTimeString(days, hours, minutes, seconds);
    }

    /**
     * 构建时间字符串，自动省略为0的单位
     */
    private static String buildTimeString(long days, long hours, long minutes, long seconds) {
        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        if (seconds > 0 || sb.isEmpty()) {
            sb.append(seconds).append("秒");
        }

        return sb.toString();
    }

    /**
     * 计算两个时间戳之间的时间差并格式化
     */
    public static String formatBetween(long startMillis, long endMillis) {
        long diff = Math.abs(endMillis - startMillis);
        return format(diff);
    }

    /**
     * 将毫秒时间戳转换为指定格式的字符串（指定时区）
     * @param timestamp 毫秒时间戳
     * @param pattern 格式模式
     * @return 格式化后的时间字符串
     */
    public static String format(long timestamp, String pattern) {
        if (timestamp == 0) return "null";
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}
