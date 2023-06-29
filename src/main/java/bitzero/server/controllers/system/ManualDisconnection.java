/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.system;

import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.server.api.IBZApi;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.entities.User;

public class ManualDisconnection
extends BaseControllerCommand {
    public ManualDisconnection() {
        super(SystemRequest.ManualDisconnection);
    }

    @Override
    public boolean validate(IRequest request) {
        return true;
    }

    @Override
    public void execute(IRequest request) throws Exception {
        User sender = this.api.getUserBySession(request.getSender());
        if (sender != null) {
            sender.setReconnectionSeconds(0);
        }
    }
}

