/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.params.BasicHttpParams
 *  org.apache.http.params.HttpParams
 *  org.apache.http.util.EntityUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.util;

import bitzero.engine.io.Response;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.CreateRoomSettings;
import bitzero.server.api.IBZApi;
import bitzero.server.config.ConfigHandle;
import bitzero.server.config.DefaultConstants;
import bitzero.server.config.ZoneSettings;
import bitzero.server.controllers.SystemRequest;
import bitzero.server.core.BZEvent;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.core.IBZEventListener;
import bitzero.server.core.IBZEventManager;
import bitzero.server.core.IBZEventType;
import bitzero.server.entities.Room;
import bitzero.server.entities.User;
import bitzero.server.entities.Zone;
import bitzero.server.entities.managers.IExtensionManager;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.entities.managers.IZoneManager;
import bitzero.server.exceptions.BZCreateRoomException;
import bitzero.server.exceptions.BZException;
import bitzero.server.exceptions.BZLoginException;
import bitzero.server.extensions.BaseBZExtension;
import bitzero.server.extensions.ExtensionLogLevel;
import bitzero.server.extensions.IBZExtension;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.server.util.ClientDisconnectionReason;
import bitzero.server.util.CryptoUtils;
import bitzero.server.util.IDisconnectionReason;
import bitzero.util.cache.KittyCache;
import bitzero.util.common.business.CommonHandle;
import bitzero.util.common.business.Debug;
import bitzero.util.config.bean.ConstantMercury;
import bitzero.util.datacontroller.business.DataController;
import bitzero.util.game.GuestLogin;
import bitzero.util.payment.BalancePacketReceive;
import bitzero.util.payment.ItemInfo;
import bitzero.util.payment.PromoPacketReceive;
import bitzero.util.payment.PurchaseInfoReceive;
import bitzero.util.socialcontroller.bean.UserInfo;
import bitzero.util.socialcontroller.exceptions.SocialControllerException;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionUtility
implements IBZEventListener {
    private static ExtensionUtility _instance = null;
    private static final Object lock = new Object();
    private static final int CACHING_TIME = 800;
    private static IBZApi bzApi = BitZeroServer.getInstance().getAPIManager().getBzApi();
    public static IUserManager globalUserManager = BitZeroServer.getInstance().getUserManager();
    public static BitZeroServer bz = BitZeroServer.getInstance();
    protected final Logger logger;
    protected KittyCache dataCache;

    private ExtensionUtility() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.dataCache = new KittyCache(200000);
        bz.getEventManager().addEventListener(BZEventType.USER_DISCONNECT, this);
        bz.getEventManager().addEventListener(BZEventType.USER_LOGIN, this);
    }

    public static BaseBZExtension getExtension() {
        return (BaseBZExtension)BitZeroServer.getInstance().getExtensionManager().getMainExtension();
    }

    public void setCache(int uId, String index, Object value) {
        try {
            this.dataCache.put("" + (uId + 95) + index, value, 800);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            CommonHandle.writeErrLogDebug("Error Set KittyCache:", uId, Character.valueOf('_'), index);
        }
    }

    public Object getCache(int uId, String index) {
        try {
            return this.dataCache.get("" + (uId + 95) + index);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            CommonHandle.writeErrLogDebug("Error getCache KittyCache:" + uId + '_' + index);
            return null;
        }
    }

    public void removeKey(int uId, String index) {
        try {
            this.dataCache.remove("" + (uId + 95) + index);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            CommonHandle.writeErrLogDebug("Error Removekey KittyCache:", uId, Character.valueOf('_'), index);
        }
    }

    public void setCache(String nickName, String index, Object value) {
        try {
            this.dataCache.put(nickName + '_' + index, value, 800);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            CommonHandle.writeErrLogDebug("Error Set KittyCache:", nickName, Character.valueOf('_'), index);
        }
    }

    public Object getCache(String nickName, String index) {
        try {
            return this.dataCache.get(nickName + '_' + index);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            CommonHandle.writeErrLogDebug("Error getCache KittyCache:" + nickName + '_' + index);
            return null;
        }
    }

    public void removeKey(String nickName, String index) {
        try {
            this.dataCache.remove(nickName + '_' + index);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            CommonHandle.writeErrLogDebug("Error Removekey KittyCache:", nickName, Character.valueOf('_'), index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ExtensionUtility instance() {
        if (_instance == null) {
            Object object = lock;
            synchronized (object) {
                if (_instance == null) {
                    _instance = new ExtensionUtility();
                }
            }
        }
        return _instance;
    }

    public Zone createChannel(int id, String name, int maxUser, int maxRoom) {
        ZoneSettings zoneSetting = new ZoneSettings();
        zoneSetting.id = id;
        zoneSetting.name = name;
        zoneSetting.maxUsers = maxUser;
        zoneSetting.maxRooms = maxRoom;
        Zone z = null;
        try {
            z = BitZeroServer.getInstance().getZoneManager().createZone(zoneSetting);
        }
        catch (BZException e) {
            ExtensionUtility.getExtension().trace("Error when creating Zone ", id, name);
        }
        return z;
    }

    public int joinChannel(User user, int toChannelId) {
        HashMap<BZEventParam, Object> evtParams;
        if (user.getJoinedRoom() != null) {
            return -1;
        }
        Zone toZone = BitZeroServer.getInstance().getZoneManager().getZoneById(toChannelId);
        if (toZone.isFull()) {
            return -2;
        }
        Zone fromZone = user.getZone();
        if (fromZone != null) {
            if (fromZone.getId() == toChannelId) {
                return -1;
            }
            fromZone.removeUser(user);
            evtParams = new HashMap();
            evtParams.put(BZEventParam.USER, user);
            evtParams.put(BZEventParam.ZONE, fromZone);
            bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.CHANGE_CHANNEL_SUCCESS, evtParams));
        }
        try {
            if (toZone != null) {
                toZone.login(user, false);
            }
        }
        catch (BZLoginException bzle) {
            ExtensionUtility.getExtension().trace(ExtensionLogLevel.DEBUG, "Zone Full", toChannelId);
            return -2;
        }
        evtParams = new HashMap<BZEventParam, Object>();
        evtParams.put(BZEventParam.USER, user);
        evtParams.put(BZEventParam.ZONE, toZone);
        bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.JOIN_CHANNEL_SUCCESS, evtParams));
        return 0;
    }

    public void outChannel(User u) {
        Zone zone = u.getZone();
        if (zone == null) {
            return;
        }
        zone.removeUser(u);
        u.setZone(null);
        HashMap<BZEventParam, Object> evtParams = new HashMap<BZEventParam, Object>();
        evtParams.put(BZEventParam.USER, u);
        evtParams.put(BZEventParam.ZONE, zone);
        bz.getEventManager().dispatchEvent(new BZEvent(BZEventType.OUT_CHANNEL_SUCCESS, evtParams));
    }

    public User canLogin(UserInfo userInfo, String password, ISession session) {
        int isOk = 0;
        User u = null;
        if (userInfo != null) {
            long maxUsers = ConfigHandle.instance().getLong("max_users");
            long numUsers = globalUserManager.getAllUsers().size();
            User currentUser = globalUserManager.getUserByName(userInfo.getUsername());
            if (currentUser != null) {
                bzApi.disconnectUser(currentUser, ClientDisconnectionReason.LOGIN);
                bzApi.disconnectUser(currentUser, true);
            }
            if (numUsers >= maxUsers) {
                isOk = 2;
            }
        } else {
            isOk = 1;
        }
        if (isOk == 0) {
            u = new User(userInfo.getUsername(), session);
            u.setId(Integer.valueOf(userInfo.getUserId()));
            u.setProperty("user_info", userInfo);
            u.setProperty("sessionKey", password);
        }

        Debug.trace(" Check request cmd Call login All");
        bzApi.login(session, (byte)isOk, u);
        return u;
    }

    public int joinRoom(User u, Room r, int nChair) {
        if (u.getJoinedRoom() != null) {
            return -2;
        }
        try {
            bzApi.joinRoom(u, r, nChair);
        }
        catch (Exception e) {
            CommonHandle.writeDebugLog(e);
            if (u.getJoinedRoom() != null) {
                return 0;
            }
            return -1;
        }
        return 0;
    }

    public int joinRoom(User u, Room r) {
        if (u.getJoinedRoom() != null) {
            return -2;
        }
        try {
            bzApi.joinRoom(u, r);
        }
        catch (Exception e) {
            CommonHandle.writeDebugLog(e);
            if (u.getJoinedRoom() != null) {
                return 0;
            }
            return -1;
        }
        return 0;
    }

    public int joinRoom(User user, Room roomToJoin, String password, boolean asSpectator) throws Exception {
        if (user.getJoinedRoom() != null) {
            return -2;
        }
        bzApi.joinRoom(user, roomToJoin, password, asSpectator, null);
        return 0;
    }

    public int joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, int nChair, boolean isHolding) throws Exception {
        this.logger.info("XU LI JOIN ROOM *** ");
        if (user.getJoinedRoom() != null) {
            return -2;
        }
        bzApi.joinRoom(user, roomToJoin, password, asSpectator, null, nChair, isHolding);
        return 0;
    }

    public int joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, int nChair) throws Exception {
        this.logger.info("XU LI JOIN ROOM *** ");
        if (user.getJoinedRoom() != null) {
            return -2;
        }
        bzApi.joinRoom(user, roomToJoin, password, asSpectator, null, nChair, false);
        return 0;
    }

    public void outRoom(User u, Room r) {
        if (r == null) {
            return;
        }
        try {
            bzApi.leaveRoom(u, r);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
    }

    public boolean outRoom(User u) {
        if (u.getJoinedRoom() == null) {
            return true;
        }
        try {
            bzApi.leaveRoom(u, u.getJoinedRoom());
            return true;
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            return true;
        }
    }

    public Room createRoom(Zone zone, User owner, String rName, int maxUser, int maxSpectators, String password) {
        CreateRoomSettings setting = new CreateRoomSettings();
        setting.setName(rName);
        setting.setMaxUsers(maxUser);
        setting.setMaxSpectators(maxSpectators);
        setting.setPassword(password);
        setting.setGame(true);
        Room room = null;
        try {
            room = bzApi.createRoom(zone, setting, owner);
        }
        catch (BZCreateRoomException e) {
            CommonHandle.writeInfoLog(e);
            room = null;
        }
        return room;
    }

    public Room createRoom(Zone zone, User owner, String rName, int maxUser, int maxSpectators, String password, boolean joinIt, boolean fireEvent) {
        CreateRoomSettings setting = new CreateRoomSettings();
        setting.setName(rName);
        setting.setMaxUsers(maxUser);
        setting.setMaxSpectators(maxSpectators);
        setting.setPassword(password);
        setting.setGame(true);
        Room room = null;
        try {
            room = bzApi.createRoom(zone, setting, owner, joinIt, null, fireEvent, fireEvent);
        }
        catch (BZCreateRoomException e) {
            CommonHandle.writeInfoLog(e);
            room = null;
        }
        return room;
    }

    public void disconnectUser(User u) {
        bzApi.disconnectUser(u);
    }

    public void removeRoom(Room room) {
        bzApi.removeRoom(room);
    }

    public static void dispatchEvent(IBZEvent se) {
        bz.getEventManager().dispatchEvent(se);
    }

    public static void dispatchImmediateEvent(IBZEvent se) {
        bz.getEventManager().dispatchImmediateEvent(se);
    }

    public static void trackingSource(String source, String uId) {
        Debug.trace((Object)"Tracking Source");
        StringBuilder keyBd = new StringBuilder(ConstantMercury.PREFIX_SNSGAME_GENERAL);
        keyBd.append(uId).append("_tracking_source");
        String key = keyBd.toString();
        try {
            if (DataController.getController().get(key) != null) {
                return;
            }
            DataController.getController().set(key, source);
        }
        catch (Exception e) {
            CommonHandle.writeErrLogDebug("Exception set Source tracking");
            CommonHandle.writeErrLog(e);
        }
    }

    public static UserInfo getUserInfo(String sessionKey) throws SocialControllerException, Exception {
        Debug.trace("sessionkey", sessionKey);
        if (sessionKey.startsWith("__")) {
            return GuestLogin.guestLogin(sessionKey);
        }
        if (sessionKey.startsWith("***")) {
            return GuestLogin.openLogin(sessionKey.substring(3));
        }
        if (sessionKey.startsWith("+++")) {
            return ExtensionUtility.getUserInfoFormPortal(sessionKey.substring(3));
        }
        if (!sessionKey.startsWith("+++")) {
            CommonHandle.writeErrLogDebug("----- ERROR SESSION LOGIN ZING ME: ", sessionKey);
            return null;
        }
        CommonHandle.writeErrLogDebug("----- ERROR SESSION LOGIN ZING ME: ZingMeAPI ", sessionKey);
        return null;
    }

    public static UserInfo getUserInfoFormPortal(String sessionKey) throws SocialControllerException, Exception {
        int i;
        if (sessionKey.startsWith("__")) {
            return GuestLogin.guestLogin(sessionKey);
        }
        if (sessionKey.startsWith("***")) {
            return GuestLogin.openLogin(sessionKey.substring(3));
        }
        String sessionTracking = sessionKey;
        if (sessionKey.contains("|||")) {
            int index = sessionKey.indexOf("|||");
            sessionKey = sessionKey.substring(0, index);
        }
        Debug.trace((Object)("session UnDecode : " + sessionKey));
        String ssKey = new String(Base64.decodeBase64((byte[])sessionKey.getBytes()));
        Debug.trace((Object)("session Decode:" + ssKey));
        String[] arrInfo = ssKey.split("&");
        if (arrInfo.length < 8) {
            Debug.trace((Object)"lenght < 8 session return");
            return null;
        }
        for (i = 0; i < arrInfo.length; ++i) {
            String[] temp = arrInfo[i].split("=");
            arrInfo[i] = temp.length >= 2 ? temp[1] : "";
        }
        if (!ExtensionUtility.isValidSessionKey(arrInfo, sessionKey)) {
            return null;
        }
        for (i = 0; i < arrInfo.length; ++i) {
            arrInfo[i] = URLDecoder.decode(arrInfo[i], "UTF-8");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(arrInfo[0]);
        userInfo.setUsername(arrInfo[1]);
        userInfo.setDisplayname(arrInfo[3]);
        userInfo.setHeadurl(arrInfo[4]);
        if (userInfo != null && userInfo.getHeadurl().toLowerCase().contains("noavatar")) {
            userInfo.setHeadurl("http://zingplay.static.g6.zing.vn/images/zpp/zpdefault.png");
        }
        try {
            Debug.trace("Session key : arrInfo[6] : other:", arrInfo[6]);
            String[] other = arrInfo[6].split("::");
            if (!(other.length < 1 || other[0].equals("") || other[0].equals("null") || other[0].equals("default"))) {
                userInfo.setTrackingSource(other[0]);
            }
            if (other.length >= 1) {
                ExtensionUtility.trackingSource(other[0], userInfo.getUserId());
            }
            if (!"".equals(userInfo.getUsername()) && !"".equals(userInfo.getUserId())) {
                StringBuilder keyBuilder = new StringBuilder(ConstantMercury.PREFIX_SNSGAME_GENERAL);
                keyBuilder.append(userInfo.getUsername()).append("_username");
                String key = keyBuilder.toString();
                Object ObjID = DataController.getController().get(key);
                if (!(other.length < 2 || other[1].equals("") || other[1].equals("null") || other[1].equals("default"))) {
                    Debug.trace("Session key : other[1]:", other[1]);
                    String[] arrTracking = other[1].split("\\|", -1);
                    if (arrTracking.length >= 1) {
                        String key_sourcePay;
                        StringBuilder key_typeUserBd = new StringBuilder(ConstantMercury.PREFIX_SNSGAME_GENERAL);
                        key_typeUserBd.append(userInfo.getUserId()).append("_tracking_user_type");
                        String key_typeUser = key_typeUserBd.toString();
                        String objTypeUser = (String)DataController.getController().get(key_typeUser);
                        Debug.trace("key_typeUser : ", key_typeUser);
                        Debug.trace("value key_typeUser : ", objTypeUser);
                        for (int i2 = 0; i2 < arrTracking.length; ++i2) {
                            Debug.trace("arrTraking :", i2, ":", arrTracking[i2]);
                        }
                        if (objTypeUser == null || "".equalsIgnoreCase(objTypeUser) || "cotuong".equalsIgnoreCase(objTypeUser)) {
                            Debug.trace("Session key : other[1]: arrTracking[0] :", arrTracking[0]);
                            DataController.getController().set(key_typeUser, arrTracking[0]);
                        }
                        if (ObjID == null && arrTracking.length >= 3) {
                            StringBuilder key_sourceAppBd = new StringBuilder(ConstantMercury.PREFIX_SNSGAME_GENERAL);
                            key_sourceAppBd.append(userInfo.getUserId()).append("_tracking_source_app");
                            String key_sourceApp = key_sourceAppBd.toString();
                            Debug.trace("Session key : other[1]: arrTracking[1] :", arrTracking[1]);
                            if (objTypeUser == null) {
                                DataController.getController().set(key_sourceApp, arrTracking[1]);
                            }
                            StringBuilder key_sourceMarketBd = new StringBuilder(ConstantMercury.PREFIX_SNSGAME_GENERAL);
                            key_sourceMarketBd.append(userInfo.getUserId()).append("_tracking_source_marketing");
                            String key_sourceMarket = key_sourceMarketBd.toString();
                            Debug.trace("Session key : other[1]: arrTracking[2] : ", arrTracking[2]);
                            if (objTypeUser == null) {
                                DataController.getController().set(key_sourceMarket, arrTracking[2]);
                            }
                        }
                        if (arrTracking.length >= 4) {
                            userInfo.setPlatform(arrTracking[3]);
                        }
                        if (arrTracking.length >= 5) {
                            StringBuilder key_sourcePayBd = new StringBuilder(ConstantMercury.PREFIX_SNSGAME_GENERAL);
                            key_sourcePayBd.append(userInfo.getUserId()).append("_tracking_source_pay");
                            key_sourcePay = key_sourcePayBd.toString();
                            Debug.trace("Session key : other[1]: arrTracking[5] :", arrTracking[4]);
                            DataController.getController().set(key_sourcePay, arrTracking[4]);
                        } else {
                            Debug.trace((Object)"no arrTraking length < 5");
                            StringBuilder key_sourcePayBd = new StringBuilder(ConstantMercury.PREFIX_SNSGAME_GENERAL);
                            key_sourcePayBd.append(userInfo.getUserId()).append("_tracking_source_pay");
                            key_sourcePay = key_sourcePayBd.toString();
                            DataController.getController().delete(key_sourcePay);
                        }
                    }
                }
                if (ObjID == null) {
                    DataController.getController().set(key, userInfo.getUserId());
                } else {
                    try {
                        String strUid = (String)ObjID;
                        int uId = Integer.parseInt(strUid);
                        if (uId < 0) {
                            throw new Exception();
                        }
                    }
                    catch (Exception e) {
                        DataController.getController().set(key, userInfo.getUserId());
                    }
                }
            }
            if (other.length >= 3) {
                userInfo.setOpenId(other[2]);
            }
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
        return userInfo;
    }

    public static boolean isValidSessionKey(String[] arrInfo, String sessionKey) throws Exception {
        try {
            String ssKey = new String(Base64.decodeBase64((byte[])sessionKey.getBytes()));
            if ("0".equalsIgnoreCase(arrInfo[0]) || "".equals(arrInfo[1])) {
                CommonHandle.writeErrLogDebug("----- ERROR SESSION : " + sessionKey);
                CommonHandle.writeErrLogDebug("----- ERROR SESSION DECODE : " + ssKey);
                return false;
            }
            try {
                int uId = Integer.parseInt(arrInfo[0]);
                if (uId <= 0) {
                    CommonHandle.writeErrLogDebug("----- ERROR SESSION DECODE : " + ssKey);
                    return false;
                }
            }
            catch (NumberFormatException nfe) {
                CommonHandle.writeErrLogDebug("----- ERROR SESSION DECODE : " + ssKey);
                return false;
            }
            StringBuilder infoBd = new StringBuilder("id=");
            infoBd.append(arrInfo[0]).append("&username=").append(arrInfo[1]).append("&social=").append(arrInfo[2]).append("&socialname=").append(arrInfo[3]).append("&avatar=").append(arrInfo[4]).append("&time=").append(arrInfo[5]);
            String info = infoBd.toString();
            Debug.trace("info", info);
            String key = ConstantMercury.PRODUCTID + ConstantMercury.SECRET_KEY;
            Debug.trace("gameKEy: ", key);
            String gameKey = CryptoUtils.getMD5Hash(key);
            Debug.trace("game Key encrty:", gameKey);
            String token = CryptoUtils.getMD5Hash(info + gameKey);
            Debug.trace("tooken: ", token);
            Debug.trace("arrg[6] : ", arrInfo[6]);
            Debug.trace("arrg[7] : ", arrInfo[7]);
            return token.equals(arrInfo[7]);
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
            String info = "";
            for (int i = 0; i < arrInfo.length; ++i) {
                info = info + arrInfo[i];
            }
            CommonHandle.writeErrLogDebug("----- ERROR SESSION MOBILE Khong login dc, session: ", sessionKey);
            return false;
        }
    }

    public static String encryptMD5(String input) {
        byte[] defaultBytes = input.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte[] messageDigest = algorithm.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; ++i) {
                String hex = Integer.toHexString(255 & messageDigest[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (NoSuchAlgorithmException nsae) {
            CommonHandle.writeErrLog(nsae);
            return null;
        }
    }

    public static UserInfo randomUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.randomData();
        return userInfo;
    }

    public PurchaseInfoReceive purchase(UserInfo userInfo, int numMoney, ItemInfo item) {
        return this.purchase(userInfo, numMoney, item, ConstantMercury.URL_REQUEST_BILLING);
    }

    public PurchaseInfoReceive purchase(UserInfo userInfo, int numMoney, ItemInfo item, String serviceKey) {
        if (!ConstantMercury.ENABLE_PAYMENT) {
            return null;
        }
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("serviceName", "purchase");
            params.put("uId", userInfo.getUserId());
            params.put("userName", userInfo.getUsername());
            params.put("money", String.valueOf(numMoney));
            params.put("itemInfo", item.toString());
            JSONArray jsonArray = null;
            CommonHandle.writePaymentLog("purchase|req|" + params);
            jsonArray = this.executeHttpRequest(params, serviceKey);
            CommonHandle.writePaymentLog("purchase|res|" + jsonArray.toString());
            int errorCode = 0;
            errorCode = jsonArray.getInt(0);
            if (errorCode != 1) {
                return null;
            }
            PurchaseInfoReceive result = new PurchaseInfoReceive(jsonArray.getJSONObject(1));
            if (result.RetCode != 1) {
                return null;
            }
            return result;
        }
        catch (Exception e) {
            CommonHandle.writePaymentLog("purchase|res|Exception: " + e.getStackTrace());
            CommonHandle.writeErrLog(e);
            return null;
        }
    }

    public void sendLoginOK(User user) {
        if (user == null || user.isBot()) {
            return;
        }
        this.sendLoginResponse(user, 0);
    }

    public void sendLoginResponse(User user, int err) {
        if (user == null || user.isBot()) {
            return;
        }
        Response response = new Response();
        response.setId(Short.parseShort(SystemRequest.Login.getId().toString()));
        response.setTargetController(DefaultConstants.CORE_EXTENSIONS_CONTROLLER_ID);
        response.setContent(new byte[]{(byte)err});
        response.setRecipients(user.getSession());
        response.write();
    }

    public void sendLoginResponse(ISession session, int err) {
        Response response = new Response();
        response.setId(Short.parseShort(SystemRequest.Login.getId().toString()));
        response.setTargetController(DefaultConstants.CORE_EXTENSIONS_CONTROLLER_ID);
        response.setContent(new byte[]{(byte)err});
        response.setRecipients(session);
        response.write();
    }

    public long inquiryBalance(UserInfo userInfo) {
        if (!ConstantMercury.ENABLE_PAYMENT) {
            return -2;
        }
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("serviceName", "balance");
            params.put("uId", userInfo.getUserId());
            params.put("userName", userInfo.getUsername());
            JSONArray jsonArray = null;
            jsonArray = this.executeHttpRequest(params);
            int errorCode = jsonArray.getInt(0);
            if (errorCode != 1) {
                return -1;
            }
            BalancePacketReceive result = new BalancePacketReceive(jsonArray.getJSONObject(1));
            if (result.RetCode != 1) {
                return -1;
            }
            return result.CashRemain;
        }
        catch (Exception e) {
            return -1;
        }
    }

    public long promo(UserInfo userInfo, int numMoney) {
        if (!ConstantMercury.ENABLE_PAYMENT) {
            return -2;
        }
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("serviceName", "promo");
            params.put("uId", userInfo.getUserId());
            params.put("userName", userInfo.getUsername());
            params.put("money", String.valueOf(numMoney));
            JSONArray jsonArray = null;
            CommonHandle.writePaymentLog("promo|req|" + params);
            jsonArray = this.executeHttpRequest(params);
            CommonHandle.writePaymentLog("promo|res|" + jsonArray.toString());
            int errorCode = 0;
            errorCode = jsonArray.getInt(0);
            if (errorCode != 1) {
                return -1;
            }
            PromoPacketReceive result = new PromoPacketReceive(jsonArray.getJSONObject(1));
            if (result.RetCode != 1) {
                return -1;
            }
            return result.CashRemain;
        }
        catch (Exception e) {
            CommonHandle.writePaymentLog("promo|res|Exception error");
            return -1;
        }
    }

    private JSONArray executeHttpRequest(Map<String, String> params) throws IOException, ClientProtocolException, JSONException {
        return this.executeHttpRequest(params, ConstantMercury.URL_REQUEST_BILLING);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JSONArray executeHttpRequest(Map<String, String> params, String urlBilling) throws IOException, ClientProtocolException, JSONException {
        String result = null;
        try {
            StringBuilder url = new StringBuilder(urlBilling);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            }
            url = url.deleteCharAt(url.length() - 1);
            BasicHttpParams httpParams = new BasicHttpParams();
            DefaultHttpClient client = new DefaultHttpClient((HttpParams)httpParams);
            this.logger.info("--Payment: url for Payment : " + url);
            HttpGet httpGet = new HttpGet(url.toString());
            HttpResponse response = null;
            try {
                response = client.execute((HttpUriRequest)httpGet);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString((HttpEntity)entity, (String)"UTF-8");
                this.logger.info("----Payment: response from Payment : " + result);
            }
            catch (Exception entity) {}
            finally {
                if (response != null && response.getEntity() != null) {
                    response.getEntity().consumeContent();
                }
            }
        }
        catch (Exception e) {
            CommonHandle.writeErrLog(e);
        }
        return new JSONArray(result);
    }

    @Override
    public void handleServerEvent(IBZEvent ibzevent) {
    }

    public void send(BaseMsg bmsg, User user) {
        if (user == null || user.isBot()) {
            return;
        }
        List<ISession> recipients = null;
        recipients = Arrays.asList(user.getSession());
        Response response = new Response();
        response.setId(bmsg.getId());
        response.setTargetController(DefaultConstants.CORE_EXTENSIONS_CONTROLLER_ID);
        response.setContent(bmsg.createData());
        response.setRecipients(recipients);
        response.write();
    }
}

