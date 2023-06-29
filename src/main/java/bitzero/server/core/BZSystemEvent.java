/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.core;

import bitzero.server.core.BZEvent;
import bitzero.server.core.IBZEventParam;
import bitzero.server.core.IBZEventType;
import java.util.Map;

public class BZSystemEvent
extends BZEvent {
    private final Map sysParams;

    public BZSystemEvent(IBZEventType type, Map params, Map sysParams) {
        super(type, params);
        this.sysParams = sysParams;
    }

    public Object getSysParameter(IBZEventParam key) {
        return this.sysParams.get(key);
    }

    public void setSysParameter(IBZEventParam key, Object value) {
        if (this.sysParams != null) {
            this.sysParams.put(key, value);
        }
    }
}

