/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.api.response;

import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.config.DefaultConstants;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.util.ClientDisconnectionReason;
import bitzero.server.util.IDisconnectionReason;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseApi {
    protected final Logger log;

    public ResponseApi() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public void sendExtResponse(BaseMsg bmsg, User user) {
        if (user == null || user.isBot()) {
            return;
        }
        List<ISession> recipients = null;
        recipients = Arrays.asList(user.getSession());
        Response response = new Response();
        response.setId(bmsg.getId());
        response.setTargetController(DefaultConstants.CORE_EXTENSIONS_CONTROLLER_ID);
        response.setContent(bmsg.createData());
        response.setRecipients(recipients);
        response.write();
    }

    public void sendExtResponse(BaseMsg bmsg, ISession session) {
        List<ISession> recipients = null;
        recipients = Arrays.asList(session);
        Response response = new Response();
        response.setId(bmsg.getId());
        response.setTargetController(DefaultConstants.CORE_EXTENSIONS_CONTROLLER_ID);
        response.setContent(bmsg.createData());
        response.setRecipients(recipients);
        response.write();
    }

    public void sendExtResponse(BaseMsg bmsg, Collection<ISession> recipients) {
        Response response = new Response();
        response.setId(bmsg.getId());
        response.setTargetController(DefaultConstants.CORE_EXTENSIONS_CONTROLLER_ID);
        response.setContent(bmsg.createData());
        response.setRecipients(recipients);
        response.write();
    }

    public void notifyClientSideDisconnection(User user, IDisconnectionReason reason) {
        if (user == null || user.isBot()) {
            return;
        }
        Response response = new Response();
        response.setId(Short.parseShort(SystemRequest.OnClientDisconnection.getId().toString()));
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.setContent(new byte[]{reason.getByteValue()});
        response.setRecipients(user.getSession());
        response.write();
    }

    public void notifyReconnectionFailure(ISession recipient) {
        Response response = new Response();
        response.setId(SystemRequest.OnClientDisconnection.getId());
        response.setRecipients(recipient);
        response.setContent(new byte[]{ClientDisconnectionReason.HANDSHAKE.getByteValue()});
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.write();
        System.out.println("SENDING TO -------> " + recipient);
    }
}

