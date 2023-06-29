/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.system;

import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.api.IBZApi;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.system.cmd.ControlParamCmd;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.exceptions.BZLoginException;
import bitzero.server.extensions.data.DataCmd;
import java.nio.ByteBuffer;

public class KickUser
extends BaseControllerCommand {
    public static final String KEY_USER_ID = "u";
    public static final String KEY_MESSAGE = "m";
    public static final String KEY_DELAY = "d";

    public KickUser() {
        super(SystemRequest.KickUser);
    }

    @Override
    public boolean validate(IRequest request) {
        return this.checkSuperAdmin(request.getSender());
    }

    @Override
    public void execute(IRequest request) throws Exception {
        DataCmd dataparams = new DataCmd(((ByteBuffer)request.getContent()).array());
        ControlParamCmd params = new ControlParamCmd(dataparams);
        params.unpackData();
        if (params.param.length < 2) {
            return;
        }
        User userToKick = this.bz.getUserManager().getUserByName(params.command);
        if (userToKick == null) {
            throw new BZLoginException(String.format("KickRequest failed. No user exist with Id: %s, requested by ", params.command, request.getSender()));
        }
        this.api.kickUser(userToKick, null, params.param[0], Integer.valueOf(params.param[1]));
    }
}

