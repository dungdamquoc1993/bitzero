/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol;

import bitzero.engine.controllers.AbstractController;
import bitzero.engine.io.IRequest;
import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import java.util.ArrayList;
import java.util.List;

public class DefaultRequestController
extends AbstractController {
    @Override
    public void processRequest(IRequest request) throws Exception {
    }

    private void handleReqOne(IRequest request) {
    }

    private void sendResponse(short actionId, Object recipients, Object responseObject) {
        ArrayList<ISession> recipientList = null;
        if (recipients instanceof List) {
            recipientList = (ArrayList<ISession>)recipients;
        } else if (recipients instanceof ISession) {
            recipientList = new ArrayList<ISession>();
            recipientList.add((ISession)recipients);
        } else {
            throw new IllegalArgumentException("Wrong recipients argument in sendResponse!");
        }
        Response response = new Response();
        response.setId(this.id);
        response.write();
    }
}

