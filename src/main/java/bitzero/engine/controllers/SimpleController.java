/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.controllers;

import bitzero.engine.controllers.AbstractController;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.io.IRequest;
import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class SimpleController
extends AbstractController {
    private final BitZeroEngine engine = BitZeroEngine.getInstance();
    private final ISessionManager sessionManager = this.engine.getSessionManager();

    @Override
    public void processRequest(IRequest request) throws Exception {
        String reqId = (String)request.getId();
        if (reqId.equals("generic")) {
            this.handleGenericRequest(request);
        } else if (reqId.equals("delay")) {
            this.handleDelayedRequest(request);
        } else if (reqId.equals("msg")) {
            if (request.getContent().equals("big")) {
                this.handleBigMessage(request);
            } else {
                this.handleChatMessage(request);
            }
        } else if (reqId.equals("restart")) {
            this.handleRestart();
        }
    }

    private void handleRestart() {
        BitZeroEngine.getInstance().restart();
    }

    private void handleChatMessage(IRequest request) {
        Response response = new Response();
        response.setId(response.getId());
        response.setContent(request.getContent());
        response.setRecipients(this.sessionManager.getAllSessions());
        response.write();
    }

    private void handleDelayedRequest(IRequest request) {
        Response response = new Response();
        response.setId(response.getId());
        response.setContent("** This is a delayed response **");
        String expectedDelayValue = (String)request.getContent();
        int delay = 2;
        delay = Integer.parseInt(expectedDelayValue);
        ArrayList<ISession> recs = new ArrayList<ISession>();
        recs.add(request.getSender());
        response.setRecipients(recs);
        response.write(delay);
    }

    private void handleGenericRequest(IRequest request) {
        Response response = new Response();
        response.setId(response.getId());
        response.setContent("Thanks for your generic message: " + request.getContent());
        ArrayList<ISession> recs = new ArrayList<ISession>();
        recs.add(request.getSender());
        response.setRecipients(recs);
        response.write();
    }

    private void handleBigMessage(IRequest request) {
        char[] rawData = new char[262144];
        Arrays.fill(rawData, 'J');
        Response response = new Response();
        response.setId(response.getId());
        response.setContent("Big Message: " + new String(rawData) + ", and have a nice day!");
        ArrayList<ISession> recs = new ArrayList<ISession>();
        recs.add(request.getSender());
        response.setRecipients(recs);
        response.write();
    }
}

