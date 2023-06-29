/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.utils;

import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.extensions.data.DataCmd;
import java.nio.ByteBuffer;

public abstract class BaseAdminCommand
extends BaseControllerCommand {
    public BaseAdminCommand(SystemRequest request) {
        super(request);
    }

    @Override
    public boolean validate(IRequest irequest) {
        return this.checkSuperAdmin(irequest.getSender());
    }

    @Override
    public void execute(IRequest irequest) throws Exception {
        DataCmd cmd = new DataCmd(((ByteBuffer)irequest.getContent()).array());
        this.handleRequest(irequest.getSender(), cmd);
    }

    public abstract void handleRequest(ISession var1, DataCmd var2);
}

