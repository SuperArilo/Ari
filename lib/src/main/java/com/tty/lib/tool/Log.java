package com.tty.lib.tool;


import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static boolean DEBUG;

    private static final AtomicReference<Logger> LOGGER = new AtomicReference<>();
    private static final String st = "[DEBUG] ";
    public static void info(String s) {
        getLogger().info(s);
    }
    public static void warning(String s) {
        getLogger().warning(s);
    }
    public static void error(String s) {
        getLogger().log(Level.SEVERE, s);
    }
    public static void error(String s, Throwable throwable) {
        getLogger().log(Level.SEVERE, s, throwable);
    }
    public static void debug(Level level, String s) {
        if(DEBUG) {
            getLogger().log(level, st + s);
        }
    }
    public static void debug(Level level, String s, Throwable throwable) {
        if(DEBUG) {
            getLogger().log(level,st + s, throwable);
        }
    }
    public static void debug(String s) {
        if(DEBUG) {
            getLogger().log(Level.INFO, st + s);
        }
    }
    public static void initLogger(Logger logger, boolean d) {
        LOGGER.set(logger);
        DEBUG = d;
    }
    private static Logger getLogger() {
        return LOGGER.get();
    }
}
