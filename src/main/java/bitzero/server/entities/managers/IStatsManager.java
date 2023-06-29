/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.entities.managers;

import bitzero.server.core.ICoreService;
import bitzero.server.entities.managers.ConnectionStats;
import bitzero.server.util.stats.INetworkTrafficMeter;

public interface IStatsManager
extends ICoreService {
    public long getTotalOutBytes();

    public long getTotalOutBytesWebsocket();

    public long getTotalInBytes();

    public long getTotalInBytesWebsocket();

    public long getTotalOutPackets();

    public long getTotalOutPacketsWebsocket();

    public long getTotalInPackets();

    public long getTotalInPacketsWebsocket();

    public long getTotalOutgoingDroppedPackets();

    public long getTotalIncomingDroppedPackets();

    public long getTotalIncomingDroppedWebsocketFrames();

    public long getTotalIncomingDroppedWebsocketPackets();

    public INetworkTrafficMeter getIncomingTrafficMeter();

    public INetworkTrafficMeter getOutgoingTrafficMeter();

    public ConnectionStats getSessionStats();

    public ConnectionStats getUserStats();
}

