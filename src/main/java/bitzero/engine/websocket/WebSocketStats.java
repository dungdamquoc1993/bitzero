/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.websocket;

public class WebSocketStats {
    private volatile long readBytes = 0;
    private volatile long readPackets = 0;
    private volatile long writtenBytes = 0;
    private volatile long writtenPackets = 0;
    private volatile long droppedInPackets = 0;
    private volatile long droppedInFrames = 0;

    public void addDroppedInPacket() {
        ++this.droppedInPackets;
    }

    public long getDroppedInPackets() {
        return this.droppedInPackets;
    }

    public void addDroppedInFrame() {
        ++this.droppedInFrames;
    }

    public long getDroppedInFrames() {
        return this.droppedInFrames;
    }

    public void addReadBytes(int value) {
        this.readBytes += (long)value;
    }

    public void addReadPackets(int value) {
        this.readPackets += (long)value;
    }

    public void addWrittenBytes(int value) {
        this.writtenBytes += (long)value;
    }

    public void addWrittenPackets(int value) {
        this.writtenPackets += (long)value;
    }

    public long getReadBytes() {
        return this.readBytes;
    }

    public long getReadPackets() {
        return this.readPackets;
    }

    public long getWrittenBytes() {
        return this.writtenBytes;
    }

    public long getWrittenPackets() {
        return this.writtenPackets;
    }
}

