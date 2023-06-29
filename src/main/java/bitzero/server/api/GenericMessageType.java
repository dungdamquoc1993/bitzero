/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.api;

public enum GenericMessageType {
    PUBLIC_MSG(0),
    PRIVATE_MSG(1),
    MODERATOR_MSG(2),
    ADMING_MSG(3),
    OBJECT_MSG(4),
    BUDDY_MSG(5);
    
    private int id;

    private GenericMessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

