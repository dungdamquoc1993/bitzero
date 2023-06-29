/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.api;

import java.util.Map;

public class SystemEvent {
    public SystemEventType type;
    private Object source;
    private Map<String, Object> params;

    public SystemEvent(Object source, Map<String, Object> params) {
        this.source = source;
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public static enum SystemEventType {
        LOGIN,
        DISCONNECT,
        CREATE_ROOM,
        JOIN_ROOM,
        DELETE_ROOM,
        OUT_ROOM,
        JOIN_CHANNEL,
        OUT_CHANNEL;
        

        private SystemEventType() {
        }
    }

}

