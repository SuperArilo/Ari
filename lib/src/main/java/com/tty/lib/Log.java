package com.tty.lib;

import com.tty.lib.tool.PublicFunctionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;


public class Log {

    private static volatile ComponentLogger LOGGER;
    private static final String PREFIX_DEBUG = "[DEBUG] ";
    private static volatile boolean DEBUG;
    private static final NamedTextColor[] RANDOM_COLORS = {
            NamedTextColor.AQUA, NamedTextColor.GREEN, NamedTextColor.YELLOW,
            NamedTextColor.GOLD, NamedTextColor.LIGHT_PURPLE, NamedTextColor.RED,
            NamedTextColor.DARK_AQUA, NamedTextColor.DARK_GREEN, NamedTextColor.DARK_RED
    };

    public static void init(ComponentLogger logger, boolean isDebug) {
        LOGGER = logger;
        DEBUG = isDebug;
    }

    private static boolean isLoggerNotReady() {
        return LOGGER == null;
    }

    /**
     * 将 msg 按 %s 分割，参数随机颜色，非参数部分使用 defaultColor
     */
    private static Component formatMessage(NamedTextColor defaultColor, String msg, Object... args) {
        String[] parts = msg.split("%s", -1);
        TextComponent.Builder builder = Component.text();

        int len = Math.min(parts.length, args.length + 1);
        for (int i = 0; i < len; i++) {
            if (!parts[i].isEmpty()) {
                builder.append(Component.text(parts[i], defaultColor));
            }
            if (i < args.length) {
                builder.append(Component.text(String.valueOf(args[i]), getRandomColor()));
            }
        }

        return builder.build();
    }

    private static NamedTextColor getRandomColor() {
        return RANDOM_COLORS[PublicFunctionUtils.randomGenerator(0, RANDOM_COLORS.length - 1)];
    }

    public static void info(String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.info(formatMessage(NamedTextColor.GREEN, msg, args));
    }

    public static void warn(String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.warn(formatMessage(NamedTextColor.YELLOW, msg, args));
    }

    public static void warn(Throwable throwable, String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.warn(formatMessage(NamedTextColor.YELLOW, msg, args), throwable);
    }

    public static void error(String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.error(formatMessage(NamedTextColor.RED, msg, args));
    }

    public static void error(Throwable throwable, String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.error(formatMessage(NamedTextColor.RED, msg, args), throwable);
    }

    public static void debug(String msg, Object... args) {
        if (!DEBUG || isLoggerNotReady()) return;
        TextComponent.Builder builder = Component.text()
                .append(Component.text(PREFIX_DEBUG, NamedTextColor.BLUE))
                .append(Component.text("[" + getCallerClassName() + "] ", NamedTextColor.BLUE))
                .append(formatMessage(NamedTextColor.BLUE, msg, args));

        LOGGER.info(builder.build());
    }

    public static void debug(Throwable throwable, String msg, Object... args) {
        if (!DEBUG || isLoggerNotReady()) return;
        TextComponent.Builder builder = Component.text()
                .append(Component.text(PREFIX_DEBUG, NamedTextColor.BLUE))
                .append(Component.text("[" + getCallerClassName() + "] ", NamedTextColor.BLUE))
                .append(formatMessage(NamedTextColor.BLUE, msg, args));

        LOGGER.info(builder.build(), throwable);
    }

    private static String getCallerClassName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (!className.equals(Log.class.getName()) && !className.startsWith("java.lang.Thread")) {
                return className;
            }
        }
        return "Unknown";
    }
}
