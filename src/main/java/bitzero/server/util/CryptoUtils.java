/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import bitzero.engine.sessions.ISession;
import bitzero.server.util.MD5;
import java.util.Random;

public class CryptoUtils {
    private static final String DELIMITER = "__";

    public static String getClientPassword(ISession session, String clearPass) {
        return MD5.getInstance().getHash(String.valueOf(session.getHashId()) + clearPass);
    }

    public static String getMD5Hash(String str) {
        return MD5.getInstance().getHash(str);
    }

    public static String getUniqueSessionToken(ISession session) {
        Random rnd = new Random();
        String key = String.valueOf(session.getFullIpAddress()) + "__" + String.valueOf(rnd.nextInt());
        return MD5.getInstance().getHash(key);
    }

    public static String getHexFileName(String name) {
        StringBuilder sb = new StringBuilder();
        char[] c = name.toCharArray();
        for (int i = 0; i < c.length; ++i) {
            sb.append(Integer.toHexString(c[i]));
        }
        return sb.toString();
    }
}

