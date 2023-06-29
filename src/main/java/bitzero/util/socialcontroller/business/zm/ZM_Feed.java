/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package bitzero.util.socialcontroller.business.zm;

import bitzero.util.common.business.CommonHandle;
import bitzero.util.socialcontroller.business.zm.ZM_API_Common;
import bitzero.util.socialcontroller.business.zm.ZM_Config;
import bitzero.util.socialcontroller.exceptions.SocialControllerException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class ZM_Feed
extends ZM_API_Common {
    public ZM_Feed(ZM_Config config) {
        super(config, "/feed");
    }

    public boolean publishUserActionV2(String session_key, int template_bundle_id, JSONObject template_data) {
        HashMap<String, String> arg = new HashMap<String, String>();
        arg.put("session_key", session_key);
        arg.put("template_bundle_id", String.valueOf(template_bundle_id));
        arg.put("template_data", template_data.toString());
        try {
            String string = this.callMethod("Feed.publishUserActionV2", arg);
        }
        catch (SocialControllerException e) {
            CommonHandle.writeErrLog(e);
            return false;
        }
        return true;
    }
}

