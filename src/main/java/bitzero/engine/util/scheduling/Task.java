/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.util.scheduling;

import java.util.HashMap;
import java.util.Map;

public class Task {
    private Object id;
    private Map parameters;
    private volatile boolean active = true;

    public Task() {
        this.parameters = new HashMap();
    }

    public Task(Object id) {
        this();
        this.id = id;
    }

    public Task(Object id, Map mapObj) {
        this.id = id;
        this.parameters = mapObj;
    }

    public Object getId() {
        return this.id;
    }

    public Map getParameters() {
        return this.parameters;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

