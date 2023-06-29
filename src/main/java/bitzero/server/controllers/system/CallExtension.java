/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.system;

import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.controllers.admin.helper.ExtensionCmd;
import bitzero.server.controllers.admin.utils.BaseAdminCommand;
import bitzero.server.core.BZEvent;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.server.extensions.data.DataCmd;
import java.util.HashMap;
import java.util.Map;

public class CallExtension
extends BaseAdminCommand {
    public CallExtension() {
        super(SystemRequest.CallExtension);
    }

    @Override
    public void handleRequest(ISession sender, DataCmd cmd) {
        ExtensionCmd data = new ExtensionCmd(sender, cmd);
        HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
        evtParams.put(BZEventParam.COMMAND, data);
        evtParams.put(BZEventParam.SESSION, sender);
        this.bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.EXTENSION_COMMAND, evtParams));
        data.sendReturn("OK");
    }
}

