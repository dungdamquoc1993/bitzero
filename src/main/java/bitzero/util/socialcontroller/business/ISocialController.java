/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.socialcontroller.business;

import bitzero.util.socialcontroller.bean.UserInfo;
import bitzero.util.socialcontroller.exceptions.SocialControllerException;
import java.util.List;

public interface ISocialController {
    public long getLoggedInUser(String var1) throws SocialControllerException;

    public UserInfo getUserInfo(Long var1, String var2) throws SocialControllerException;

    public List<UserInfo> getUserInfo(List<Long> var1, String var2) throws SocialControllerException;

    public List<Long> getFriendList(String var1) throws SocialControllerException;

    public boolean feedOpenApi2(String var1, int var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10);
}

