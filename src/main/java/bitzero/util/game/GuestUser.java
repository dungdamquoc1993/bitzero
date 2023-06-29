/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.game;

import bitzero.util.common.business.Debug;
import bitzero.util.datacontroller.business.DataController;
import bitzero.util.socialcontroller.bean.UserInfo;
import java.util.concurrent.atomic.AtomicInteger;

public class GuestUser {
    private static final GuestUser _instance = new GuestUser();
    public static final String GUEST_ID_KEY = "ID_GUEST";
    private static final int GUEST_START_ID = 900000000;
    private static final int GUEST_START_NAME_COUNT = 999;
    private static final String GUEST_NAME = "lang_khach_";
    private final AtomicInteger autoID = new AtomicInteger(900000000);

    private GuestUser() {
        this.init();
    }

    public static GuestUser instance() {
        return _instance;
    }

    public UserInfo getInfoByDeviceId(String deviceId) {
        int userId = 0;
        try {
            userId = (Integer)DataController.getController().get("myplay_ID_GUEST_" + deviceId);
        }
        catch (Exception var3_3) {
            // empty catch block
        }
        if (userId == 0) {
            userId = this.autoID.incrementAndGet();
            this.saveInfo(userId, deviceId);
        }
        return this.makeInfo(userId);
    }

    private void saveInfo(int userId, String deviceId) {
        try {
            DataController.getController().set("myplay_ID_GUEST_" + deviceId, userId);
            DataController.getController().set("myplay_ID_GUEST", this.autoID.get());
        }
        catch (Exception e) {
            Debug.trace((Object)("guestLogin--------------:SAVE ERRO : " + deviceId + "__" + userId));
        }
    }

    private UserInfo makeInfo(int userId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(Integer.valueOf(userId).toString());
        userInfo.setUsername("lang_khach_" + Integer.valueOf(userId - 900000000 + 999));
        userInfo.setHeadurl("");
        return userInfo;
    }

    public void init() {
        int count = 0;
        try {
            count = (Integer)DataController.getController().get("myplay_ID_GUEST");
        }
        catch (Exception var2_2) {
            // empty catch block
        }
        if (count != 0) {
            this.autoID.set(count);
        }
    }
}

