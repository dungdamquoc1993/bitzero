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

public class Logout
extends BaseControllerCommand {
    public static final String KEY_ZONE_NAME = "zn";

    public Logout() {
        super(SystemRequest.Logout);
    }

    @Override
    public boolean validate(IRequest request) {
        return true;
    }

    @Override
    public void execute(IRequest request) throws Exception {
        User sender = this.api.getUserBySession(request.getSender());
        if (sender == null) {
            throw new IllegalArgumentException("Logout failure. Session is not logged in: " + request.getSender());
        }
        this.api.logout(sender);
    }
}

