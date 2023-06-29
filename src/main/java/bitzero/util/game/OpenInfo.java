/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.game;

import bitzero.util.dao.JDBObject;

public class OpenInfo
extends JDBObject {
    public long id = -1;
    public static final String OPEN_ID_KEY = "OPENID6";
    public String openId = "";
    public int accType = 3;
    public String zmeI = "";
    public String zmeU = "";
    public String zmeP = "";

    private OpenInfo(int uId) {
        super(uId);
    }

    public static OpenInfo load(int uId) {
        OpenInfo obj = null;
        try {
            obj = (OpenInfo)JDBObject.load(uId, OpenInfo.class);
            if (obj == null) {
                obj = new OpenInfo(uId);
            }
        }
        catch (Exception var2_2) {
            // empty catch block
        }
        return obj;
    }
}

