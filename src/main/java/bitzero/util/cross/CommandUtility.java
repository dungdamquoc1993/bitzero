/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.cross;

import bitzero.util.cross.CrossConnection;
import bitzero.util.cross.CrossExtCommand;

public class CommandUtility {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void send(String commandName, String[] params, String host, int port) {
        CrossConnection conn = null;
        try {
            conn = new CrossConnection(host, port);
            CrossExtCommand cmd = new CrossExtCommand(commandName, params);
            conn.send(cmd.getByte());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}

