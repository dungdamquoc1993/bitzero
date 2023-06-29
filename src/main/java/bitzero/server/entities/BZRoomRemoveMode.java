/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities;

public enum BZRoomRemoveMode {
    DEFAULT,
    WHEN_EMPTY,
    WHEN_EMPTY_AND_CREATOR_IS_GONE,
    NEVER_REMOVE;
    

    private BZRoomRemoveMode() {
    }

    public static BZRoomRemoveMode fromString(String id) {
        return BZRoomRemoveMode.valueOf(id.toUpperCase());
    }
}

