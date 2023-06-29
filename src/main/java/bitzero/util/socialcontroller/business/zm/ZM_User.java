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
import bitzero.util.socialcontroller.bean.UserInfo;
import bitzero.util.socialcontroller.business.zm.ZM_API_Common;
import bitzero.util.socialcontroller.business.zm.ZM_Config;
import bitzero.util.socialcontroller.exceptions.SocialControllerException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZM_User
extends ZM_API_Common {
    public ZM_User(ZM_Config config) {
        super(config, "/users");
    }

    private static List<UserInfo> parseRespone(String respone) {
        try {
            LinkedList<UserInfo> result = new LinkedList<UserInfo>();
            JSONArray jsonArr = new JSONArray(respone);
            for (int i = 0; i < jsonArr.length(); ++i) {
                JSONObject json = jsonArr.getJSONObject(i);
                UserInfo info = new UserInfo();
                if (json.has("firstname")) {
                    info.setFirstname(json.getString("firstname"));
                }
                if (json.has("gender")) {
                    info.setGender(json.getString("gender"));
                }
                if (json.has("headurl")) {
                    info.setHeadurl(json.getString("headurl"));
                }
                if (json.has("lastname")) {
                    info.setLastname(json.getString("lastname"));
                }
                if (json.has("profile_url")) {
                    info.setProfile_url(json.getString("profile_url"));
                }
                if (json.has("tinyurl")) {
                    info.setTinyurl(json.getString("tinyurl"));
                }
                if (json.has("userid")) {
                    info.setUserId(json.getString("userid"));
                }
                if (json.has("username")) {
                    info.setUsername(json.getString("username"));
                }
                if (json.has("displayname")) {
                    info.setDisplayname(json.getString("displayname"));
                }
                if (json.has("dob")) {
                    info.setDob(json.getString("dob"));
                }
                if (json.has("status")) {
                    info.setStatus(json.getString("status"));
                }
                result.add(info);
            }
            return result;
        }
        catch (JSONException e) {
            return null;
        }
    }

    public List<UserInfo> getUserInfo(List<Long> uids, String filter) throws SocialControllerException {
        if (uids.size() > 0) {
            return this.getUserInfo(this.idListToString(uids), filter);
        }
        return new LinkedList<UserInfo>();
    }

    public List<UserInfo> getUserInfo(String uids, String filter) throws SocialControllerException {
        HashMap<String, String> args = new HashMap<String, String>();
        args.put("uids", uids);
        args.put("fields", filter);
        try {
            String respone = this.callMethod("Users.getInfo", args);
            return ZM_User.parseRespone(respone);
        }
        catch (SocialControllerException sce) {
            CommonHandle.writeErrLog(sce);
            return new LinkedList<UserInfo>();
        }
    }

    private String idListToString(List<Long> uids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uids.size(); ++i) {
            if (i > 0) {
                sb.append(",").append(uids.get(i));
                continue;
            }
            sb.append(uids.get(i));
        }
        return sb.toString();
    }
}

