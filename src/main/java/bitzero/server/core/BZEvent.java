/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.core;

import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventParam;
import bitzero.server.core.IBZEventType;
import java.util.Map;
import java.util.Set;

public class BZEvent
implements IBZEvent {
    private final IBZEventType type;
    private final Map params;

    public BZEvent(IBZEventType type) {
        this(type, null);
    }

    public BZEvent(IBZEventType type, Map params) {
        this.type = type;
        this.params = params;
    }

    @Override
    public IBZEventType getType() {
        return this.type;
    }

    @Override
    public Object getParameter(IBZEventParam id) {
        Object param = null;
        if (this.params != null) {
            param = this.params.get(id);
        }
        return param;
    }

    public String toString() {
        Object[] arrobject = new Object[2];
        arrobject[0] = this.type;
        arrobject[1] = this.params == null ? "none" : this.params.keySet();
        return String.format("{ %s, Params: %s }", arrobject);
    }
}

