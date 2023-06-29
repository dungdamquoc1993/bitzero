/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.game;

import bitzero.engine.sessions.ISession;
import bitzero.server.util.CryptoUtils;
import bitzero.util.common.business.Debug;
import bitzero.util.datacontroller.business.DataController;
import bitzero.util.datacontroller.business.DataControllerException;
import bitzero.util.game.GuestUser;
import bitzero.util.game.OpenInfo;
import bitzero.util.socialcontroller.bean.UserInfo;
import bitzero.util.socialcontroller.exceptions.SocialControllerException;

public class GuestLogin {
    private static final String keydefault = "nightfury";

    public static UserInfo guestLogin(String deviceId) {
        return GuestUser.instance().getInfoByDeviceId(deviceId);
    }

    public static UserInfo openLogin(String sessionKey) throws SocialControllerException, Exception {
        UserInfo userInfo = null;
        String[] openInfo = sessionKey.split("__");
        String openId = openInfo[1];
        int accType = Integer.valueOf(openInfo[0]);
        int userId = 0;
        if (accType <= 1) {
            userInfo = GuestUser.instance().getInfoByDeviceId("" + accType + "***" + openId);
            userInfo.setUsername(openInfo[2]);
            userInfo.setHeadurl(openInfo[3]);
            userId = Integer.valueOf(userInfo.getUserId());
            Debug.trace((Object)("URL--------------:" + openInfo[3]));
        }
        if (userId > 0) {
            OpenInfo dbOpenInfo = OpenInfo.load(userId);
            if (dbOpenInfo.openId.equals("") || dbOpenInfo.accType != accType) {
                dbOpenInfo.openId = openId;
                dbOpenInfo.accType = accType;
                dbOpenInfo.zmeP = "abcdef";
                dbOpenInfo.save();
            }
        }
        return userInfo;
    }

    public static String saveLoginSession(ISession sender, UserInfo userInfo) {
        if (userInfo == null || sender == null) {
            return "";
        }
        Integer userId = Integer.valueOf(userInfo.getUserId());
        String sessionKey = CryptoUtils.getUniqueSessionToken(sender);
        try {
            DataController.getController().setCache("myplay_" + sessionKey, 86400, userId.toString());
            DataController.getController().setCache("myplay_" + userId + "_ZmInfo", 86400, userInfo);
        }
        catch (DataControllerException dce) {
            dce.printStackTrace();
        }
        return sessionKey;
    }

    public static String generateLoginSession(String game, ISession sender, UserInfo userInfo) {
        if (userInfo == null || sender == null || game == null) {
            return "";
        }
        String info = "uid=" + userInfo.getUserId() + "&username=" + userInfo.getUsername() + "&avatar=" + userInfo.getHeadurl() + "&time=" + System.currentTimeMillis();
        String gameKey = CryptoUtils.getMD5Hash(game + "__" + "nightfury");
        String tokenKey = CryptoUtils.getMD5Hash(info + gameKey);
        return info + "&tokenKey=" + tokenKey;
    }
}

