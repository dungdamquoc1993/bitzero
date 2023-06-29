/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.core;

import bitzero.server.BitZeroServer;
import bitzero.server.entities.managers.IExtensionManager;
import java.io.PrintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZShutdownHook
extends Thread {
    private final Logger log;

    public BZShutdownHook() {
        super("BZ-Alpha ShutdownHook");
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void run() {
        this.log.warn("BZ-Alpha is shutting down. The process may take a few seconds...");
        BitZeroServer.getInstance().getExtensionManager().destroy();
        System.out.println("BitZeroServer is stoped");
    }
}

