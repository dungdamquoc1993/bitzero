/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.core;

import bitzero.server.core.ICoreService;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseCoreService
implements ICoreService {
    private static final AtomicInteger serviceId = new AtomicInteger(0);
    private static final String DEFAULT_NAME = "AnonymousService-";
    protected String name;
    protected volatile boolean active = false;

    @Override
    public void init(Object o) {
        this.name = BaseCoreService.getId();
        this.active = true;
    }

    @Override
    public void destroy(Object o) {
        this.active = false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void handleMessage(Object param) {
        throw new UnsupportedOperationException("This method should be overridden by the child class!");
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public String toString() {
        return "[Core Service]: " + this.name + ", State: " + (this.isActive() ? "active" : "not active");
    }

    private static String getId() {
        return "AnonymousService-" + serviceId.getAndIncrement();
    }
}

