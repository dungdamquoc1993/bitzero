/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.cmd;

import bitzero.server.controllers.SystemRequest;
import bitzero.server.extensions.data.BaseMsg;

public class SystemStatsMsg
extends BaseMsg {
    public long totalInPacket = 0;
    public long totalOutPacket = 0;
    public long totalInBytes = 0;
    public long totalOutBytes = 0;
    public long totalOutgoingDroppedPackets = 0;
    public long totalIncomingDroppedPackets = 0;
    public int connectionCount = 0;
    public int mobileCount = 0;
    public int totalUserCount = 0;
    public double cpuLoad = -1.0;
    public long memLoad = -1;
    public long totalInPacketWebsocket = 0;
    public long totalOutPacketWebsocket = 0;
    public long totalInBytesWebsocket = 0;
    public long totalOutBytesWebsocket = 0;
    public int wsCount = 0;

    public SystemStatsMsg() {
        super((Short)SystemRequest.SystemStats.getId());
    }
}

