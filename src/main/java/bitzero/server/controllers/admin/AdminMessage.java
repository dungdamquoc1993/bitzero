/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin;

import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.api.IBZApi;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.admin.utils.BaseAdminCommand;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.extensions.data.DataCmd;
import java.util.Collection;
import java.util.List;

public class AdminMessage
extends BaseAdminCommand {
    public AdminMessage() {
        super(SystemRequest.AdminMessage);
    }

    @Override
    public void handleRequest(ISession sender, DataCmd cmd) {
        this.api.sendAdminMessage(null, cmd.readString(), new String[]{"Notify User "}, this.bz.getUserManager().getAllSessions());
    }
}

