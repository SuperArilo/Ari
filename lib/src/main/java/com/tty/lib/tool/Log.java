package com.tty.lib.tool;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static boolean DEBUG;
    private static volatile Logger logger;
    private static final String PREFIX_DEBUG = "[DEBUG] ";

    public static void initLogger(Logger log, boolean debugEnabled) {
        logger = log;
        DEBUG = debugEnabled;
    }

    public static void info(String msg) {
        logger.info(format(msg));
    }

    public static void warning(String msg) {
        logger.warning(format(msg));
    }

    public static void error(String msg) {
        logger.severe(format(msg));
    }

    public static void error(String msg, Throwable throwable) {
        logger.log(Level.SEVERE, format(msg), throwable);
    }

    public static void debug(String msg) {
        if (DEBUG) {
            logger.info(PREFIX_DEBUG + format(msg));
        }
    }

    public static void debug(Level level, String msg) {
        if (DEBUG) {
            logger.log(level, PREFIX_DEBUG + format(msg));
        }
    }

    public static void debug(Level level, String msg, Throwable throwable) {
        if (DEBUG) {
            logger.log(level, PREFIX_DEBUG + format(msg), throwable);
        }
    }

    private static String format(String msg) {
        return "[" + getCallerClassName() + "] " + msg;
    }

    private static String getCallerClassName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > 4) {
            String full = stack[4].getClassName();
            int lastDot = full.lastIndexOf('.');
            return lastDot == -1 ? full : full.substring(lastDot + 1);
        }
        return "Unknown";
    }
}
