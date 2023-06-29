/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.config;

public class ControllerConfig {
    private String className;
    private Object id;
    private int threadPoolSize;
    private int maxRequestQueueSize;

    public ControllerConfig(String className, Object id, int threadPoolSize, int maxRequestQueueSize) {
        this.className = className;
        this.id = id;
        this.threadPoolSize = threadPoolSize;
        this.maxRequestQueueSize = maxRequestQueueSize;
    }

    public String getClassName() {
        return this.className;
    }

    public Object getId() {
        return this.id;
    }

    public int getThreadPoolSize() {
        return this.threadPoolSize;
    }

    public int getMaxRequestQueueSize() {
        return this.maxRequestQueueSize;
    }
}

