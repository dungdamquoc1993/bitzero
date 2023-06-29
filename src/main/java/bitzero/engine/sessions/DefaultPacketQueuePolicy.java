/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.sessions;

import bitzero.engine.data.IPacket;
import bitzero.engine.data.MessagePriority;
import bitzero.engine.exceptions.PacketQueueWarning;
import bitzero.engine.sessions.IPacketQueue;
import bitzero.engine.sessions.IPacketQueuePolicy;

public class DefaultPacketQueuePolicy
implements IPacketQueuePolicy {
    private static final int THREE_QUARTERS_FULL = 75;
    private static final int NINETY_PERCENT_FULL = 90;

    @Override
    public void applyPolicy(IPacketQueue packetQueue, IPacket packet) throws PacketQueueWarning {
        int percentageFree = packetQueue.getSize() * 100 / packetQueue.getMaxSize();
        if (percentageFree >= 75 && percentageFree < 90) {
            if (packet.getPriority().getValue() < MessagePriority.NORMAL.getValue()) {
                this.fireDropMessageError(packet, percentageFree, packetQueue.getSize());
            }
        } else if (percentageFree >= 90 && packet.getPriority().getValue() < MessagePriority.HIGH.getValue()) {
            this.fireDropMessageError(packet, percentageFree, packetQueue.getSize());
        }
    }

    private void fireDropMessageError(IPacket packet, int percentageFree, int size) {
        throw new PacketQueueWarning("Dropping packet: " + packet + ", Free queue: " + percentageFree + "%");
    }
}

