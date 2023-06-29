/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.data;

public enum TransportType {
    TCP("Tcp"),
    UDP("Udp"),
    BLUEBOX("BlueBox");
    
    String name;

    private TransportType(String name) {
        this.name = name;
    }

    public static TransportType fromName(String name) {
        for (TransportType tt : TransportType.values()) {
            if (!tt.name.equalsIgnoreCase(name)) continue;
            return tt;
        }
        throw new IllegalArgumentException("There is no TransportType definition for the requested type: " + name);
    }
}

