/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol.binary;

public enum PacketReadState {
    WAIT_NEW_PACKET,
    WAIT_DATA_SIZE,
    WAIT_DATA_SIZE_FRAGMENT,
    WAIT_DATA;
    

    private PacketReadState() {
    }
}

