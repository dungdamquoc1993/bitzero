/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package bitzero.engine.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import org.slf4j.Logger;

public class Logging {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String TAB = "\t";

    public static void changeConsoleHandlerLevel(Level newLevel) {
        Handler firstHandler;
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers.length > 0 && (firstHandler = handlers[0]) != null && firstHandler instanceof ConsoleHandler) {
            firstHandler.setLevel(newLevel);
        }
    }

    public static Level getConsoleHandlerLevel() {
        Handler firstHandler;
        Level level = null;
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers.length > 0 && (firstHandler = handlers[0]) != null && firstHandler instanceof ConsoleHandler) {
            level = firstHandler.getLevel();
        }
        return level;
    }

    public static void changeConsoleHandlerFormatter(Formatter formatter) {
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers.length > 0) {
            Handler firstHandler = handlers[0];
            if (firstHandler != null && firstHandler instanceof ConsoleHandler) {
                firstHandler.setFormatter(formatter);
            } else {
                throw new RuntimeException("Could not change the ConsoleHandler's formatter!");
            }
        }
    }

    public static void logStackTrace(Logger logger, Throwable throwable) {
        Logging.logStackTrace(logger, throwable.toString(), throwable.getStackTrace());
    }

    public static void logStackTrace(Logger logger, StackTraceElement[] stackTrace) {
        Logging.logStackTrace(logger, null, stackTrace);
    }

    public static void logStackTrace(Logger logger, String cause, StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder(NEW_LINE);
        if (cause != null) {
            sb.append(cause).append(NEW_LINE);
        }
        for (StackTraceElement element : stackTrace) {
            sb.append("\t").append(element).append(NEW_LINE);
        }
        logger.warn(sb.toString());
    }

    public static String formatStackTrace(StackTraceElement[] elements) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : elements) {
            sb.append(element).append(NEW_LINE);
        }
        return sb.toString();
    }
}

