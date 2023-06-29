/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package bitzero.engine.boot;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import org.slf4j.Logger;

public class SystemPropertiesEnumerator {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String TAB = "\t";
    private static final String[] props = new String[]{"os.name", "os.arch", "os.version", "java.version", "java.vendor", "java.vendor.url", "java.vm.specification.version", "java.vm.version", "java.vm.vendor", "java.vm.name", "java.io.tmpdir"};

    public void logProperties(Logger logger) {
        this.logSystemInfo(logger);
        this.logNetCardsInfo(logger);
    }

    private void logSystemInfo(Logger logger) {
        StringBuilder sb = new StringBuilder("System Info:").append(NEW_LINE);
        Runtime rt = Runtime.getRuntime();
        sb.append("\t").append("Processor(s): ").append(rt.availableProcessors()).append(NEW_LINE);
        sb.append("\t").append("VM Max. memory: ").append(rt.maxMemory() / 1000000).append("MB").append(NEW_LINE);
        for (String prop : props) {
            sb.append("\t").append(prop).append(": ").append(System.getProperty(prop)).append(NEW_LINE);
        }
        sb.append("\t").append("Default charset: " + Charset.defaultCharset()).append(NEW_LINE);
        logger.info(sb.toString());
    }

    private void logNetCardsInfo(Logger logger) {
        StringBuilder sb = new StringBuilder("Network Info:").append(NEW_LINE);
        try {
            Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces();
            while (list.hasMoreElements()) {
                NetworkInterface iFace = list.nextElement();
                sb.append("\t").append("Card: ").append(iFace.getDisplayName()).append(NEW_LINE);
                Enumeration<InetAddress> addresses = iFace.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress adr = addresses.nextElement();
                    sb.append("\t").append("\t").append(" ->").append(adr.getHostAddress()).append(NEW_LINE);
                }
            }
            logger.info(sb.toString());
        }
        catch (SocketException se) {
            logger.warn("Exception while discovering network cards: " + se.getMessage());
        }
    }
}

