/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package bitzero.util.socialcontroller.business.zm;

import bitzero.util.common.business.CommonHandle;
import bitzero.util.socialcontroller.business.zm.ZM_API_Common;
import bitzero.util.socialcontroller.business.zm.ZM_Config;
import bitzero.util.socialcontroller.exceptions.SocialControllerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZM_Friends
extends ZM_API_Common {
    public ZM_Friends(ZM_Config config) {
        super(config, "/friends");
    }

    private static List<Long> parseRespone(String respone) throws SocialControllerException {
        try {
            JSONObject json = new JSONObject(respone);
            ArrayList<Long> result = new ArrayList<Long>();
            JSONArray arrJson = json.getJSONArray("uid");
            for (int i = 0; i < arrJson.length(); ++i) {
                result.add(Long.parseLong(arrJson.getString(i)));
            }
            return result;
        }
        catch (JSONException e) {
            CommonHandle.writeErrLog((Throwable)e);
            CommonHandle.writeErrLog("Zingme return json format error: " + respone);
            throw new SocialControllerException(-3, "loi JSON");
        }
    }

    public List<Long> getFriendList(String sessionId) throws SocialControllerException {
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("session_key", sessionId);
        String respone = this.callMethod("Friends.getLists", args);
        return ZM_Friends.parseRespone(respone);
    }
}

