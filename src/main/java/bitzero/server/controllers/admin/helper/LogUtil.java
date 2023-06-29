/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.controllers.admin.helper;

import bitzero.engine.sessions.ISession;
import bitzero.server.util.CryptoUtils;

public class LogUtil {
    public static final String serialVersion = "2888cd106bd98b888fca74c785bd6cf5";
    public static final String serialVersionName = "LogUtilserialVersionName";

    public static boolean checkLog(ISession session, String version, String versionName) {
        return CryptoUtils.getMD5Hash(version).equalsIgnoreCase("2888cd106bd98b888fca74c785bd6cf5") && CryptoUtils.getClientPassword(session, "LogUtilserialVersionName").equalsIgnoreCase(versionName);
    }
}

