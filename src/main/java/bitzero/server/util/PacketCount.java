/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import bitzero.server.util.PacketType;
import java.util.HashMap;
import java.util.PriorityQueue;

public class PacketCount {
    public HashMap<Short, Long> packetIn = new HashMap();
    public HashMap<Short, Long> packetOut = new HashMap();
    public HashMap<Short, Long> packetInDropped = new HashMap();
    public HashMap<Short, Long> packetOutDroppedFull = new HashMap();
    public HashMap<Short, Long> packetOutDroppedWarn = new HashMap();
    public int id_max = -1;
    public long num_max = 0;
    public int id_second = -2;
    public long num_second = 0;

    public void addPacket(int type, Short packetId) {
        if (type == PacketType.INCOMING) {
            if (this.packetIn.containsKey(packetId)) {
                this.packetIn.put(packetId, this.packetIn.get(packetId) + 1);
            } else {
                this.packetIn.put(packetId, (long)1);
            }
        } else if (type == PacketType.OUTGOING) {
            if (this.packetOut.containsKey(packetId)) {
                this.packetOut.put(packetId, this.packetOut.get(packetId) + 1);
            } else {
                this.packetOut.put(packetId, (long)1);
            }
        } else if (type == PacketType.IN_DROP) {
            if (this.packetInDropped.containsKey(packetId)) {
                this.packetInDropped.put(packetId, this.packetInDropped.get(packetId) + 1);
            } else {
                this.packetInDropped.put(packetId, (long)1);
            }
        } else if (type == PacketType.OUT_DROP_FULL) {
            if (this.packetOutDroppedFull.containsKey(packetId)) {
                this.packetOutDroppedFull.put(packetId, this.packetOutDroppedFull.get(packetId) + 1);
            } else {
                this.packetOutDroppedFull.put(packetId, (long)1);
            }
        } else if (type == PacketType.OUT_DROP_WARN) {
            if (this.packetOutDroppedWarn.containsKey(packetId)) {
                this.packetOutDroppedWarn.put(packetId, this.packetOutDroppedWarn.get(packetId) + 1);
            } else {
                this.packetOutDroppedWarn.put(packetId, (long)1);
            }
        }
    }

    public void addPacket(int type, Short packetId, int userId) {
        if (type == PacketType.OUT_DROP_WARN) {
            long cur = 1;
            if (this.packetOutDroppedWarn.containsKey(packetId)) {
                cur = this.packetOutDroppedWarn.get(packetId) + 1;
            }
            this.packetOutDroppedWarn.put(packetId, cur);
            if (cur > this.num_second && userId != this.id_max) {
                this.num_second = cur;
                this.id_second = userId;
            }
            if (this.num_second > this.num_max) {
                int temp = this.id_max;
                long temp_ = this.num_max;
                this.num_max = this.num_second;
                this.id_max = this.id_second;
                this.num_second = temp_;
                this.id_second = temp;
            }
        }
    }

    public String countUserDrop() {
        PriorityQueue priQueue = new PriorityQueue();
        return "";
    }
}

