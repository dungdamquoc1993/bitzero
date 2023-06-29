/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin;

import bitzero.engine.io.IRequest;
import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.config.DefaultConstants;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.admin.cmd.SystemStatsMsg;
import bitzero.server.entities.managers.ConnectionStats;
import bitzero.server.entities.managers.IStatsManager;
import bitzero.server.entities.managers.IUserManager;

public class SystemStats
extends BaseControllerCommand {
    public SystemStats() {
        super(SystemRequest.SystemStats);
    }

    @Override
    public boolean validate(IRequest request) {
        return this.checkSuperAdmin(request.getSender());
    }

    @Override
    public void execute(IRequest request) throws Exception {
        ISession sender = request.getSender();
        SystemStatsMsg msg = new SystemStatsMsg();
        msg.totalInPacket = this.bz.getStatsManager().getTotalInPackets();
        msg.totalOutPacket = this.bz.getStatsManager().getTotalOutPackets();
        msg.totalInBytes = this.bz.getStatsManager().getTotalInBytes();
        msg.totalOutBytes = this.bz.getStatsManager().getTotalOutBytes();
        msg.totalIncomingDroppedPackets = this.bz.getStatsManager().getTotalIncomingDroppedPackets();
        msg.totalOutgoingDroppedPackets = this.bz.getStatsManager().getTotalOutgoingDroppedPackets();
        ConnectionStats connStats = this.bz.getStatsManager().getUserStats();
        msg.connectionCount = connStats.getSocketCount();
        msg.mobileCount = connStats.getTunnelledCount();
        msg.totalUserCount = this.bz.getUserManager().getUserCount();
        msg.totalInPacketWebsocket = this.bz.getStatsManager().getTotalInPacketsWebsocket();
        msg.totalOutPacketWebsocket = this.bz.getStatsManager().getTotalOutPacketsWebsocket();
        msg.totalInBytesWebsocket = this.bz.getStatsManager().getTotalInBytesWebsocket();
        msg.totalOutBytesWebsocket = this.bz.getStatsManager().getTotalOutBytesWebsocket();
        msg.wsCount = connStats.getWebsocketSessionCount();
        Response response = new Response();
        response.setId(this.getId());
        response.setRecipients(sender);
        response.setContent(msg.createData());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.write();
    }
}

