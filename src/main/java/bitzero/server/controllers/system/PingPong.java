/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.system;

import bitzero.engine.io.IRequest;
import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.api.IBZApi;
import bitzero.server.config.DefaultConstants;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.entities.User;

public class PingPong
extends BaseControllerCommand {
    public PingPong() {
        super(SystemRequest.PingPong);
    }

    @Override
    public boolean validate(IRequest irequest) {
        return true;
    }

    @Override
    public void execute(IRequest irequest) throws Exception {
        User sender = this.api.getUserBySession(irequest.getSender());
        if (sender != null) {
            Response response = new Response();
            response.setId(this.getId());
            response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
            response.setContent(new byte[]{0});
            response.setRecipients(irequest.getSender());
            response.write();
        }
    }
}

