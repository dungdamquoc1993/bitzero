/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.helper;

import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.config.DefaultConstants;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.admin.cmd.AdminCmdMsg;
import bitzero.server.util.system.ILogDevice;

public class AdminCmdLog
implements ILogDevice {
    ISession admin;

    public AdminCmdLog(ISession session) {
        this.admin = session;
    }

    @Override
    public void log(String str) {
        AdminCmdMsg msg = new AdminCmdMsg();
        msg.stringReturn = str;
        Response response = new Response();
        response.setId(SystemRequest.ExecuteCommand.getId());
        response.setRecipients(this.admin);
        response.setContent(msg.createData());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.write();
    }
}

