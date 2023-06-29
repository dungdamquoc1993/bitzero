/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.utils;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class PerformanceMonitor {
    private long lastSystemTime = 0;
    private long lastProcessCpuTime = 0;
    OperatingSystemMXBean osMxBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();

    public synchronized double getCpuUsage() {
        if (this.lastSystemTime == 0) {
            this.baselineCounters();
            return 0.0;
        }
        long systemTime = System.nanoTime();
        long processCpuTime = this.osMxBean.getProcessCpuTime();
        double cpuUsage = (double)(processCpuTime - this.lastProcessCpuTime) / (double)(systemTime - this.lastSystemTime);
        this.lastSystemTime = systemTime;
        this.lastProcessCpuTime = processCpuTime;
        return cpuUsage / (double)this.osMxBean.getAvailableProcessors();
    }

    private void baselineCounters() {
        this.lastSystemTime = System.nanoTime();
        this.lastProcessCpuTime = this.osMxBean.getProcessCpuTime();
    }
}

