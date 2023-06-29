/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

public enum BanMode {
    BY_ADDRESS(0),
    BY_NAME(1);
    
    private int id;

    private BanMode(int id) {
        this.id = id;
    }

    public static BanMode fromId(int id) {
        if (id == 0) {
            return BY_ADDRESS;
        }
        return BY_NAME;
    }

    public static BanMode fromString(String id) {
        BanMode mode = BY_NAME;
        if (id.equalsIgnoreCase("ip")) {
            mode = BY_ADDRESS;
        }
        return mode;
    }
}

