/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.helper;

import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.config.DefaultConstants;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.extensions.data.DataCmd;
import bitzero.server.extensions.data.SimpleMsg;

public class ExtensionCmd {
    public String command = "";
    public String[] params = new String[0];
    private ISession sender;

    public ExtensionCmd(ISession admin, DataCmd cmd) {
        this.command = cmd.readString();
        this.params = cmd.readStringArray();
        this.sender = admin;
    }

    public void sendReturn(String msg) {
        SimpleMsg msgData = new SimpleMsg((short)13);
        msgData.putString(msg);
        Response response = new Response();
        response.setId(SystemRequest.CallExtension.getId());
        response.setRecipients(this.sender);
        response.setContent(msgData.createData());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.write();
    }

    protected void send(ISession sender, BaseMsg msg) {
        Response response = new Response();
        response.setId(msg.getId());
        response.setRecipients(sender);
        response.setContent(msg.createData());
        response.setTargetController(DefaultConstants.CORE_SYSTEM_CONTROLLER_ID);
        response.write();
    }
}

