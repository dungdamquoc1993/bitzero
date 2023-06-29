/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.system;

import bitzero.engine.io.IRequest;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.controllers.BaseControllerCommand;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventSysParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.BZSystemEvent;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.Zone;
import bitzero.server.entities.data.ISFSObject;
import bitzero.server.entities.data.SFSObject;
import bitzero.server.entities.managers.IExtensionManager;
import bitzero.server.entities.managers.IZoneManager;
import bitzero.server.exceptions.BZRequestValidationException;
import bitzero.server.extensions.IBZExtension;
import java.util.HashMap;
import java.util.Map;

public class Login
extends BaseControllerCommand {
    public static final String KEY_USERNAME = "un";
    public static final String KEY_PASSWORD = "pw";
    public static final String KEY_ZONENAME = "zn";
    public static final String KEY_PARAMS = "p";
    public static final String KEY_PRIVILEGE_ID = "pi";
    public static final String KEY_ID = "id";
    public static final String KEY_ROOMLIST = "rl";
    public static final String KEY_RECONNECTION_SECONDS = "rs";

    public Login() {
        super(SystemRequest.LoginWebsocket);
    }

    @Override
    public boolean validate(IRequest request) throws BZRequestValidationException {
        boolean res = true;
        ISFSObject sfso = (ISFSObject)request.getContent();
        if (!(sfso.containsKey("un") && sfso.containsKey("pw") && sfso.containsKey("zn"))) {
            throw new BZRequestValidationException("Bad Login Request. Essential parameters are missing. Client API is probably fake.");
        }
        Zone zone = this.bz.getZoneManager().getZoneByName(sfso.getUtfString("zn"));
        if (zone != null && zone.isCustomLogin()) {
            if (zone.getExtension() == null) {
                throw new BZRequestValidationException("Custom login is ON but no Extension is active for this zone: " + zone.getName());
            }
            HashMap<BZEventSysParam, Object> sysParams = new HashMap<BZEventSysParam, Object>();
            sysParams.put(BZEventSysParam.NEXT_COMMAND, Login.class);
            sysParams.put(BZEventSysParam.REQUEST_OBJ, request);
            HashMap<BZEventParam, Object> userParams = new HashMap<BZEventParam, Object>();
            userParams.put(BZEventParam.ZONE, zone);
            userParams.put(BZEventParam.SESSION, request.getSender());
            userParams.put(BZEventParam.LOGIN_NAME, sfso.getUtfString("un"));
            userParams.put(BZEventParam.LOGIN_PASSWORD, sfso.getUtfString("pw"));
            userParams.put(BZEventParam.LOGIN_IN_DATA, sfso.getSFSObject("p"));
            SFSObject paramsOut = SFSObject.newInstance();
            request.setAttribute("$FS_REQUEST_LOGIN_DATA_OUT", paramsOut);
            userParams.put(BZEventParam.LOGIN_OUT_DATA, paramsOut);
            this.bz.getEventManager().dispatchEvent(new BZSystemEvent(BZEventType.USER_LOGIN, userParams, sysParams));
            res = false;
        }
        return res;
    }

    @Override
    public void executeWebsocket(IRequest request) throws Exception {
        String newUserName;
        ISFSObject reqObj = (ISFSObject)request.getContent();
        String zoneName = reqObj.getUtfString("zn");
        String userName = reqObj.getUtfString("un");
        String password = reqObj.getUtfString("pw");
        ISFSObject params = (ISFSObject)request.getAttribute("$FS_REQUEST_LOGIN_DATA_OUT");
        if (params != null && (newUserName = params.getUtfString("$FS_NEW_LOGIN_NAME")) != null) {
            userName = newUserName;
        }
        IBZExtension extension = this.bz.getExtensionManager().getMainExtension();
        extension.doLogin(request.getSender(), reqObj);
    }

    @Override
    public void execute(IRequest request) throws Exception {
    }
}

