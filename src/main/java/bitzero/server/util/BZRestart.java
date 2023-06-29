/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.util;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZRestart
extends Thread {
    private static final String LINUX_LAUNCHER = "./BZ2x.sh";
    private static final String OSX_LAUNCHER = "./BZ2x.sh";
    private static final String WIN_LAUNCHER = "BZ2x.bat";
    private final Logger log = LoggerFactory.getLogger(BZRestart.class);
    private boolean isWindows;
    private boolean isOSX;
    private boolean isLinux = false;

    public BZRestart() {
        this.setName(":::BZRestarter:::");
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("linux") != -1) {
            this.isLinux = true;
        } else if (osName.toLowerCase().indexOf("mac os x") != -1) {
            this.isOSX = true;
        } else if (osName.toLowerCase().indexOf("windows") != -1) {
            this.isWindows = true;
        } else {
            throw new IllegalStateException("Restart failure: operating system not supported: " + osName);
        }
    }

    @Override
    public void run() {
        try {
            String restartCmd = null;
            if (this.isWindows) {
                restartCmd = "BZ2x.bat";
            } else if (this.isLinux) {
                restartCmd = "./BZ2x.sh";
            } else if (this.isOSX) {
                restartCmd = "./BZ2x.sh";
            }
            String[] cmds = restartCmd.split("\\,");
            ArrayList<String> command = new ArrayList<String>();
            for (String cmd : cmds) {
                command.add(cmd);
            }
            ProcessBuilder builder = new ProcessBuilder(command);
            Process proc = builder.start();
            this.log.info("Process restarted: " + proc);
            Thread.sleep(4000);
            System.exit(-2);
        }
        catch (Exception e) {
            this.log.error("Restart exception: " + e);
        }
    }
}

