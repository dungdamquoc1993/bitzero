/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol.binary;

import bitzero.server.protocol.binary.PacketReadState;

public class ProcessedPacket {
    private byte[] data;
    private PacketReadState state;

    public ProcessedPacket(PacketReadState state, byte[] data) {
        this.state = state;
        this.data = data;
    }

    public byte[] getData() {
        return this.data;
    }

    public PacketReadState getState() {
        return this.state;
    }
}

