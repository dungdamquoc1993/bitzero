/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.entities.managers;

import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineReader;
import bitzero.engine.core.IEngineWriter;
import bitzero.engine.io.IOHandler;
import bitzero.engine.service.IService;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.websocket.WebSocketService;
import bitzero.engine.websocket.WebSocketStats;
import bitzero.server.BitZeroServer;
import bitzero.server.entities.managers.ConnectionStats;
import bitzero.server.entities.managers.IExtensionManager;
import bitzero.server.entities.managers.IStatsManager;
import bitzero.server.util.TaskScheduler;
import bitzero.server.util.stats.INetworkTrafficMeter;
import bitzero.server.util.stats.NetworkTrafficMeter;
import bitzero.server.util.stats.TrafficType;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZStatsManager
implements IStatsManager {
    private INetworkTrafficMeter inMeter;
    private INetworkTrafficMeter outMeter;
    private BitZeroEngine engine;
    private BitZeroServer bz;
    private NetworkStatsExecutor statsExecutor;
    private ScheduledFuture<?> taskControl;
    private WebSocketService webSocketService;
    private WebSocketStats wsStats;

    @Override
    public long getTotalOutBytesWebsocket() {
        return this.wsStats.getWrittenBytes();
    }

    @Override
    public long getTotalInBytesWebsocket() {
        return this.wsStats.getReadBytes();
    }

    @Override
    public long getTotalOutPacketsWebsocket() {
        return this.wsStats.getWrittenPackets();
    }

    @Override
    public long getTotalInPacketsWebsocket() {
        return this.wsStats.getReadPackets();
    }

    @Override
    public long getTotalIncomingDroppedWebsocketFrames() {
        return this.wsStats.getDroppedInFrames();
    }

    @Override
    public long getTotalIncomingDroppedWebsocketPackets() {
        return this.wsStats.getDroppedInPackets();
    }

    @Override
    public void init(Object o) {
        this.engine = BitZeroEngine.getInstance();
        this.bz = BitZeroServer.getInstance();
        this.inMeter = new NetworkTrafficMeter(TrafficType.INCOMING);
        this.outMeter = new NetworkTrafficMeter(TrafficType.OUTGOING);
        this.statsExecutor = new NetworkStatsExecutor();
        this.taskControl = this.bz.getTaskScheduler().scheduleAtFixedRate(this.statsExecutor, 0, 1, TimeUnit.MINUTES);
        this.webSocketService = (WebSocketService)this.engine.getServiceByName("webSocketEngine");
        this.wsStats = this.webSocketService.getWebSocketStats();
    }

    @Override
    public void destroy(Object obj) {
        this.taskControl.cancel(true);
    }

    @Override
    public long getTotalInPackets() {
        return this.engine.getEngineReader().getReadPackets();
    }

    @Override
    public long getTotalOutPackets() {
        return this.engine.getEngineWriter().getWrittenPackets();
    }

    @Override
    public long getTotalInBytes() {
        return this.engine.getEngineReader().getReadBytes();
    }

    @Override
    public long getTotalOutBytes() {
        return this.engine.getEngineWriter().getWrittenBytes();
    }

    @Override
    public long getTotalOutgoingDroppedPackets() {
        return this.engine.getEngineWriter().getDroppedPacketsCount();
    }

    @Override
    public INetworkTrafficMeter getIncomingTrafficMeter() {
        return this.inMeter;
    }

    @Override
    public INetworkTrafficMeter getOutgoingTrafficMeter() {
        return this.outMeter;
    }

    @Override
    public long getTotalIncomingDroppedPackets() {
        return this.engine.getEngineReader().getIOHandler().getIncomingDroppedPackets();
    }

    @Override
    public ConnectionStats getSessionStats() {
        List<ISession> allSessions = this.bz.getSessionManager().getAllLocalSessions();
        int socketCount = 0;
        int npcCount = 0;
        boolean bboxCount = false;
        int wsCount = 0;
        for (ISession session : allSessions) {
            if (session.isMobile()) {
                ++npcCount;
                continue;
            }
            if (session.isWebsocket()) {
                ++wsCount;
                continue;
            }
            ++socketCount;
        }
        return new ConnectionStats(socketCount, npcCount, npcCount, wsCount);
    }

    @Override
    public ConnectionStats getUserStats() {
        return this.getSessionStats();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public String getName() {
        return "StatsManager Service";
    }

    @Override
    public void handleMessage(Object message) {
        throw new UnsupportedOperationException("Not available");
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not available");
    }

    private class NetworkStatsExecutor
    implements Runnable {
        private final Logger log;

        @Override
        public void run() {
            try {
                BZStatsManager.this.inMeter.onTick();
                BZStatsManager.this.outMeter.onTick();
                BZStatsManager.this.bz.getExtensionManager().monitor();
            }
            catch (Exception e) {
                this.log.warn("Unexpected exception: " + e + ". NetworkStatsExecutor will resume on next call.");
            }
        }

        public NetworkStatsExecutor() {
            this.log = LoggerFactory.getLogger(NetworkStatsExecutor.class);
        }
    }

}

