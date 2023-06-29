/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.controllers;

import bitzero.engine.controllers.AbstractController;
import bitzero.engine.io.IRequest;
import bitzero.server.BitZeroServer;
import bitzero.server.controllers.IControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.util.PacketCount;
import bitzero.server.util.PacketType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemController
extends AbstractController {
    private static final Map commandMap = new HashMap();
    private static final String commandPackage = "bitzero.server.controllers.system.";
    private static final String adminPackage = "bitzero.server.controllers.admin.";
    private static final String gamePackage = "game.";
    private final BitZeroServer bz = BitZeroServer.getInstance();
    private final Logger logger;
    private Map<Object, IControllerCommand> commandCache;
    private boolean useCache;

    public SystemController() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.useCache = true;
        this.useCache = true;
    }

    @Override
    public void init(Object o) {
        super.init(o);
        this.commandCache = new ConcurrentHashMap<Object, IControllerCommand>();
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
    }

    @Override
    public void processRequest(IRequest request) throws Exception {
        block6 : {
            this.logger.info("{IN}: " + SystemRequest.fromId(request.getId()).toString());
            IControllerCommand command = null;
            Object reqId = request.getId();
            if (this.useCache) {
                command = this.commandCache.get(reqId);
                if (command == null) {
                    command = this.getCommand(reqId);
                    this.commandCache.put(reqId, command);
                }
            } else {
                command = this.getCommand(reqId);
            }
            if (command != null && command.validate(request)) {
                try {
                    BitZeroServer.getInstance().getPacketCount().addPacket(PacketType.INCOMING, (Short)reqId);
                    command.execute(request);
                }
                catch (BZRuntimeException re) {
                    String msg = re.getMessage();
                    if (msg == null) break block6;
                    this.logger.warn(msg);
                }
            }
        }
    }

    private IControllerCommand getCommand(Object reqId) {
        IControllerCommand command = null;
        String className = (String)commandMap.get(reqId);
        if (className != null) {
            try {
                Class clazz = Class.forName(className);
                command = (IControllerCommand)clazz.newInstance();
            }
            catch (Exception err) {
                this.logger.error("Could not dynamically instantiate class: " + className + ", Error: " + err);
            }
        } else {
            this.logger.error("Cannot find a controller command for request ID: " + reqId);
        }
        return command;
    }

    static {
        commandMap.put(SystemRequest.Handshake.getId(), "bitzero.server.controllers.system.Handshake");
        commandMap.put(SystemRequest.Login.getId(), "bitzero.server.controllers.admin.AdminLogin");
        commandMap.put(SystemRequest.LoginWebsocket.getId(), "bitzero.server.controllers.system.Login");
        commandMap.put(SystemRequest.Logout.getId(), "bitzero.server.controllers.system.Logout");
        commandMap.put(SystemRequest.AdminMessage.getId(), "bitzero.server.controllers.admin.AdminMessage");
        commandMap.put(SystemRequest.GenericMessage.getId(), "bitzero.server.controllers.system.GenericMessage");
        commandMap.put(SystemRequest.ObjectMessage.getId(), "bitzero.server.controllers.system.SendObject");
        commandMap.put(SystemRequest.CallExtension.getId(), "bitzero.server.controllers.system.CallExtension");
        commandMap.put(SystemRequest.CrossExtCommand.getId(), "bitzero.server.controllers.system.CrossExtCommand");
        commandMap.put(SystemRequest.KickUser.getId(), "bitzero.server.controllers.system.KickUser");
        commandMap.put(SystemRequest.BanUser.getId(), "bitzero.server.controllers.system.BanUser");
        commandMap.put(SystemRequest.ManualDisconnection.getId(), "bitzero.server.controllers.system.ManualDisconnection");
        commandMap.put(SystemRequest.SystemStats.getId(), "bitzero.server.controllers.admin.SystemStats");
        commandMap.put(SystemRequest.ExecuteCommand.getId(), "bitzero.server.controllers.admin.ExecuteCommand");
        commandMap.put(SystemRequest.SetPoolSize.getId(), "bitzero.server.controllers.admin.SetPoolSize");
        commandMap.put(SystemRequest.SetLogLevel.getId(), "bitzero.server.controllers.admin.SetLogLevel");
        commandMap.put(SystemRequest.PingPong.getId(), "bitzero.server.controllers.system.PingPong");
        commandMap.put(SystemRequest.IpFilterCommand.getId(), "bitzero.server.controllers.admin.IpFilterCommand");
        commandMap.put(SystemRequest.CrossCommand.getId(), "bitzero.server.controllers.system.CrossCommand");
        commandMap.put(SystemRequest.ServiceNotify.getId(), "bitzero.server.controllers.system.ServiceNotify");
        commandMap.put(SystemRequest.DashBoard.getId(), "bitzero.server.controllers.admin.DashBoardCommand");
        commandMap.put(SystemRequest.CheckOnline.getId(), "bitzero.server.controllers.admin.CheckOnline");
        commandMap.put(SystemRequest.BanUserChat.getId(), "bitzero.server.controllers.system.BanUserChat");
    }
}

