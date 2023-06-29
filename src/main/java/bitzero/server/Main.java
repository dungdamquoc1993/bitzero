/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server;

import bitzero.server.BitZeroServer;

public class Main {
    public static void main(String[] args) {
        boolean clusterMode = false;
        boolean useConsole = false;
        if (args.length > 0) {
            clusterMode = args[0].equalsIgnoreCase("cluster");
            useConsole = args.length > 1 && args[1].equalsIgnoreCase("console");
        }
        BitZeroServer bzServer = BitZeroServer.getInstance();
        bzServer.setClustered(clusterMode);
        if (useConsole) {
            bzServer.startDebugConsole();
        }
        bzServer.start();
    }
}

