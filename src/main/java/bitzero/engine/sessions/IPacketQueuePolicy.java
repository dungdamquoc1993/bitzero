/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.sessions;

import bitzero.engine.data.IPacket;
import bitzero.engine.exceptions.PacketQueueWarning;
import bitzero.engine.sessions.IPacketQueue;

public interface IPacketQueuePolicy {
    public void applyPolicy(IPacketQueue var1, IPacket var2) throws PacketQueueWarning;
}

