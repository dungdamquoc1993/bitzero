/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import bitzero.server.util.IDisconnectionReason;

public enum ClientDisconnectionReason implements IDisconnectionReason
{
    IDLE(0),
    KICK(1),
    BAN(2),
    LOGIN(3),
    UNKNOWN(4),
    HANDSHAKE(5);
    
    private final int value;

    private ClientDisconnectionReason(int id) {
        this.value = id;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public byte getByteValue() {
        return (byte)this.value;
    }
}

