/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.system;

import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.system.cmd.ControlParamCmd;
import bitzero.server.core.BZEvent;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.server.extensions.data.DataCmd;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class BanUserChat
extends BaseControllerCommand {
    public BanUserChat() {
        super(SystemRequest.BanUserChat);
    }

    @Override
    public boolean validate(IRequest request) {
        return this.checkSuperAdmin(request.getSender());
    }

    @Override
    public void execute(IRequest request) {
        DataCmd dataparams = new DataCmd(((ByteBuffer)request.getContent()).array());
        ControlParamCmd params = new ControlParamCmd(dataparams);
        params.unpackData();
        HashMap<BZEventParam, String> evtParams = new HashMap<BZEventParam, String>();
        evtParams.put(BZEventParam.RECIPIENT, params.command);
        //evtParams.put(BZEventParam.COMMAND, (String)params.param);
        evtParams.put(BZEventParam.COMMAND, String.join(",",params.param));
        BZEvent banUserChat = new BZEvent(BZEventType.BAN_USER_CHAT, evtParams);
        this.bz.getEventManager().dispatchEvent(banUserChat);
    }

    @Override
    protected Boolean checkSuperAdmin(ISession session) {
        if (session.getProperty("SuperAdmin") != null) {
            return true;
        }
        return false;
    }
}

