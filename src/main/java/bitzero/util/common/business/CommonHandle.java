/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package bitzero.util.common.business;

import bitzero.server.exceptions.ExceptionMessageComposer;
import bitzero.server.util.DebugConsole;
import bitzero.util.common.business.Debug;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.slf4j.Logger;

public class CommonHandle {
    private CommonHandle() {
    }

    public static void writeActionLog(String ownerId, String victimId, String ipOrUrl, String action, String param1, String param2, String param3, String param4, String param5, int goldChange, int xuChange, int expChange) {
        String data = ownerId + "|" + victimId + "|" + ipOrUrl + "|" + action + "|" + param1 + "|" + param2 + "|" + param3 + "|" + param4 + "|" + param5 + "|" + goldChange + "|" + xuChange + "|" + expChange;
        DebugConsole.log.error(data);
    }

    public static void writeErrLog(Throwable t) {
        if (Debug.DEBUG) {
            t.printStackTrace();
        }
        ExceptionMessageComposer msg = new ExceptionMessageComposer(t);
        msg.setDescription("An error occurred during the Execution");
        DebugConsole.log.error(msg.toString());
    }

    public static /* varargs */ void writeErrLogDebug(Object ... args) {
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            msg.append(args[i]).append(" ");
        }
        DebugConsole.log.error(msg.toString());
    }

    public static void writeErrLogDebug(String msg) {
        DebugConsole.log.error(msg.toString());
    }

    public static void writeWarnLog(Throwable t) {
        if (Debug.DEBUG) {
            t.printStackTrace();
        }
        ExceptionMessageComposer msg = new ExceptionMessageComposer(t);
        msg.setDescription("An warning occurred during the Execution");
        DebugConsole.log.warn(msg.toString());
    }

    public static void writeDebugLog(Throwable t) {
        if (Debug.DEBUG) {
            t.printStackTrace();
        }
        ExceptionMessageComposer msg = new ExceptionMessageComposer(t);
        msg.setDescription("An warning occurred during the Execution");
        DebugConsole.log.debug(msg.toString());
    }

    public static void writeInfoLog(Throwable t) {
        if (Debug.DEBUG) {
            t.printStackTrace();
        }
        ExceptionMessageComposer msg = new ExceptionMessageComposer(t);
        msg.setDescription("An warning occurred during the Execution");
        DebugConsole.log.info(msg.toString());
    }

    public static void writeErrLog(String s) {
        DebugConsole.log.error(s);
    }

    public static void writePaymentLog(String data) {
        DebugConsole.log.error(data);
    }

    public static void writeInfoLog(String data) {
        DebugConsole.log.error(data);
    }

    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
}

