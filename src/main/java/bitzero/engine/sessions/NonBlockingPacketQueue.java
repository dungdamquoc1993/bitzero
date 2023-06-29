/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.sessions;

import bitzero.engine.data.IPacket;
import bitzero.engine.exceptions.MessageQueueFullException;
import bitzero.engine.exceptions.PacketQueueWarning;
import bitzero.engine.sessions.IPacketQueue;
import bitzero.engine.sessions.IPacketQueuePolicy;
import java.util.LinkedList;

public final class NonBlockingPacketQueue
implements IPacketQueue {
    private final LinkedList queue = new LinkedList();
    private int maxSize;
    private IPacketQueuePolicy packetQueuePolicy;

    public NonBlockingPacketQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        LinkedList linkedList = this.queue;
        synchronized (linkedList) {
            this.queue.clear();
        }
    }

    @Override
    public int getSize() {
        return this.queue.size();
    }

    @Override
    public int getMaxSize() {
        return this.maxSize;
    }

    @Override
    public boolean isEmpty() {
        return this.queue.size() == 0;
    }

    @Override
    public boolean isFull() {
        return this.queue.size() >= this.maxSize;
    }

    @Override
    public float getPercentageUsed() {
        if (this.maxSize == 0) {
            return 0.0f;
        }
        return (float)(this.queue.size() * 100) / (float)this.maxSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IPacket peek() {
        IPacket packet = null;
        LinkedList linkedList = this.queue;
        synchronized (linkedList) {
            if (!this.isEmpty()) {
                packet = (IPacket)this.queue.getFirst();
            }
        }
        return packet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void put(IPacket packet) throws MessageQueueFullException, PacketQueueWarning {
        if (this.isFull()) {
            throw new MessageQueueFullException();
        }
        this.packetQueuePolicy.applyPolicy(this, packet);
        LinkedList linkedList = this.queue;
        synchronized (linkedList) {
            this.queue.addLast(packet);
        }
    }

    @Override
    public void setMaxSize(int size) {
        this.maxSize = size;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IPacket take() {
        IPacket packet = null;
        LinkedList linkedList = this.queue;
        synchronized (linkedList) {
            packet = (IPacket)this.queue.removeFirst();
        }
        return packet;
    }

    @Override
    public IPacketQueuePolicy getPacketQueuePolicy() {
        return this.packetQueuePolicy;
    }

    @Override
    public void setPacketQueuePolicy(IPacketQueuePolicy packetQueuePolicy) {
        this.packetQueuePolicy = packetQueuePolicy;
    }
}

