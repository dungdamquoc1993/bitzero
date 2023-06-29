/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers;

public enum SuperUserSendMode {
    TO_USER(0),
    TO_ROOM(1),
    TO_GROUP(2),
    TO_ZONE(3);
    
    private int modeId;

    private SuperUserSendMode(int id) {
        this.modeId = id;
    }

    public int getId() {
        return this.modeId;
    }

    public static SuperUserSendMode fromId(int id) {
        SuperUserSendMode mode = null;
        for (SuperUserSendMode item : SuperUserSendMode.values()) {
            if (item.getId() != id) continue;
            mode = item;
            break;
        }
        return mode;
    }
}

