/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.util.monitor.business;

import bitzero.server.config.ConfigHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterMonitor
implements Runnable {
    private static String SERVERS_INFO = ConfigHandle.instance().get("servers_key") == null ? "servers" : ConfigHandle.instance().get("servers_key");
    private Runtime runtime;
    private String cmdSql;
    private String ip = ConfigHandle.instance().get("server_ip");
    private String serverName = ConfigHandle.instance().get("server_name");
    private long maxUsers = ConfigHandle.instance().getLong("max_users");
    private long numUsers = 0;
    private int port = ConfigHandle.instance().getLong("server_port").intValue();
    private final Logger logger;

    public ClusterMonitor() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        String table = ConfigHandle.instance().get("table_metrics");
        this.logger.info("Server_Key: " + SERVERS_INFO);
        this.cmdSql = "insert into " + table + " (time, ip, ccu, cpu, ram) values(?,?,?,?,?)";
        this.runtime = Runtime.getRuntime();
    }

    @Override
    public void run() {
        this.logConcurrentUsersPerServer();
        if (System.getProperty("os.name").indexOf("Linux") >= 0) {
            this.setSystemStat();
        }
    }

    private void logConcurrentUsersPerServer() {
    }

    private void setSystemStat() {
    }
}

