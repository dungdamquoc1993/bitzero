/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol.binary;

import bitzero.server.protocol.binary.PacketHeader;

public class PendingPacket {
    private PacketHeader header;
    private Object buffer;

    public PendingPacket(PacketHeader header) {
        this.header = header;
    }

    public PacketHeader getHeader() {
        return this.header;
    }

    public Object getBuffer() {
        return this.buffer;
    }

    public void setBuffer(Object buffer) {
        this.buffer = buffer;
    }

    public String toString() {
        return String.valueOf(this.header.toString()) + this.buffer.toString();
    }
}

