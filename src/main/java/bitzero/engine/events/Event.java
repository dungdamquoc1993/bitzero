/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.events;

import bitzero.engine.events.IEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Event
implements IEvent {
    protected Object target;
    protected String name;
    protected Map params;

    public Event(String name) {
        this.name = name;
    }

    public Event(String name, Object source) {
        this.target = source;
        this.name = name;
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getParameter(String key) {
        Object param = null;
        if (this.params != null) {
            param = this.params.get(key);
        }
        return param;
    }

    @Override
    public void setParameter(String key, Object value) {
        if (this.params == null) {
            this.params = new ConcurrentHashMap();
        }
        this.params.put(key, value);
    }

    public String toString() {
        return "Event { Name:" + this.name + ", Source: " + this.target + ", Params: " + this.params + " }";
    }
}

