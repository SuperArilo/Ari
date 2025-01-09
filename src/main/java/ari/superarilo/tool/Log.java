package ari.superarilo.tool;

import ari.superarilo.Ari;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
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
        if(Ari.debug) {
            getLogger().log(level, st + s);
        }
    }
    public static void debug(Level level, String s, Throwable throwable) {
        if(Ari.debug) {
            getLogger().log(level,st + s, throwable);
        }
    }
    public static void debug(String s) {
        if(Ari.debug) {
            getLogger().log(Level.INFO, st + s);
        }
    }
    public static void setLogger(Logger logger) {
        LOGGER.set(logger);
    }
    private static Logger getLogger() {
        return LOGGER.get();
    }
}
