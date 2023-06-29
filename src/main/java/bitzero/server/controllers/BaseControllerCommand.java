/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.controllers;

import bitzero.engine.io.IRequest;
import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.IBZApi;
import bitzero.server.config.DefaultConstants;
import bitzero.server.controllers.IControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import bitzero.server.exceptions.BZRuntimeException;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.security.PrivilegeManager;
import bitzero.server.security.SystemPermission;
import bitzero.server.util.IFloodFilter;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseControllerCommand
implements IControllerCommand {
    public static final String KEY_SUPER_ADMIN = "SuperAdmin";
    protected final Logger logger;
    protected final BitZeroServer bz;
    protected final IBZApi api;
    private short id;
    private final SystemRequest requestType;
    private final List superUserRequest;

    public BaseControllerCommand(SystemRequest request) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.bz = BitZeroServer.getInstance();
        this.superUserRequest = Arrays.asList(new SystemRequest[]{SystemRequest.ModeratorMessage, SystemRequest.AdminMessage});
        this.api = this.bz.getAPIManager().getBzApi();
        this.id = (Short)request.getId();
        this.requestType = request;
    }

    @Override
    public Object preProcess(IRequest request) throws Exception {
        return null;
    }

    public short getId() {
        return this.id;
    }

    protected Boolean checkSuperAdmin(ISession session) {
        if (session.getProperty("SuperAdmin") != null) {
            return true;
        }
        return false;
    }

    protected void setAdmin(ISession session, String name) {
        session.setProperty("SuperAdmin", name);
        session.setConnected(true);
        session.setMaxLoggedInIdleTime(86400);
        session.setMaxIdleTime(86400);
    }

    protected void send(ISession sender, BaseMsg msg) {
        Response response = new Response();
        response.setId(this.getId());
        response.setRecipients(sender);
        response.setContent(msg.createData());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.write();
    }

    public SystemRequest getRequestType() {
        return this.requestType;
    }

    protected String getAdminName(ISession session) {
        return (String)session.getProperty("SuperAdmin");
    }

    protected User checkRequestPermissions(IRequest request, SystemRequest requestType) {
        User sender = this.api.getUserBySession(request.getSender());
        if (sender == null) {
            throw new BZRuntimeException(String.format("System Request rejected: %s, Client is not logged in. ", request.getSender()));
        }
        if (sender.isBeingKicked()) {
            throw new BZRuntimeException(String.format("System Request rejected: %s, Client is marked for kicking. ", sender));
        }
        Zone zone = sender.getZone();
        try {
            zone.getFloodFilter().filterRequest(requestType, sender);
        }
        catch (Exception floodErr) {
            throw new BZRuntimeException();
        }
        boolean isValid = false;
        isValid = this.superUserRequest.contains((Object)requestType) ? zone.getPrivilegeManager().isFlagSet(sender, SystemPermission.SuperUser) : zone.getPrivilegeManager().isRequestAllowed(sender, requestType);
        if (!isValid) {
            throw new BZRuntimeException(String.format("System Request rejected: insufficient privileges. Request: %s, User: %s", new Object[]{requestType, sender}));
        }
        return sender;
    }

    protected User checkRequestPermissions(IRequest request) {
        return this.checkRequestPermissions(request, SystemRequest.fromId(this.id));
    }

    @Override
    public void executeWebsocket(IRequest request) throws Exception {
    }
}

