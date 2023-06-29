/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.sessions;

import bitzero.engine.data.IPacket;
import bitzero.engine.exceptions.MessageQueueFullException;
import bitzero.engine.sessions.IPacketQueuePolicy;

public interface IPacketQueue {
    public IPacket peek();

    public IPacket take();

    public boolean isEmpty();

    public boolean isFull();

    public int getSize();

    public int getMaxSize();

    public void setMaxSize(int var1);

    public float getPercentageUsed();

    public void clear();

    public void put(IPacket var1) throws MessageQueueFullException;

    public IPacketQueuePolicy getPacketQueuePolicy();

    public void setPacketQueuePolicy(IPacketQueuePolicy var1);
}

