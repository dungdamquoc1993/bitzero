/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.socialcontroller.business.zm;

import bitzero.util.socialcontroller.business.zm.ZM_API_Common;
import bitzero.util.socialcontroller.business.zm.ZM_Config;
import bitzero.util.socialcontroller.exceptions.SocialControllerException;
import java.util.HashMap;
import java.util.Map;

public class ZM_Session
extends ZM_API_Common {
    public ZM_Session(ZM_Config config) {
        super(config, "/sessions");
    }

    public long getLoggedInUser(String sessionKey) throws SocialControllerException {
        long userId = -1;
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("session_key", sessionKey);
        String result = this.callMethod("Users.getLoggedInUser", args);
        try {
            userId = Long.parseLong(result);
        }
        catch (Exception var6_5) {
            // empty catch block
        }
        return userId;
    }
}

