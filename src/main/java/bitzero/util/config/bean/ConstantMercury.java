/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.config.bean;

import bitzero.server.config.ConfigHandle;

public class ConstantMercury {
    public static final int SCRIBE_PORT = ConfigHandle.instance().get("scribe_port") == null ? 1463 : ConfigHandle.instance().getLong("scribe_port").intValue();
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final int MAX_MESSAGE_LENGTH = 65535;
    public static final String PREFIX_SNSGAME_GENERAL = ConfigHandle.instance().get("games") + "_";
    public static final String SUFFIX_ZM_SESSION = "_ZmSession";
    public static final String SUFFIX_ZM_INFO = "_ZmInfo";
    public static final String SUFFIX_ZM_USER_ID = "_ZmId";
    public static final String SUFFIX_ZM_FRIENDS = "_ZmFriendList";
    public static final String SUFFIX_XU = "_xu";
    public static final String SUFFIX_ONLINE = "_online";
    public static final String SUFFIX_SERVERIP = "_serverip";
    public static final String SUFFIX_TRACKING_SOURCE = "_tracking_source";
    public static final String SUFFIX_TRACKING_USER_TYPE = "_tracking_user_type";
    public static final String SUFFIX_TRACKING_SOURCE_APP = "_tracking_source_app";
    public static final String SUFFIX_TRACKING_SOURCE_MARKETING = "_tracking_source_marketing";
    public static final String SUFFIX_TRACKING_SOURCE_PAY = "_tracking_source_pay";
    public static final String URL_REQUEST_BILLING = ConfigHandle.instance().get("url_billing");
    public static final String PRODUCTID = ConfigHandle.instance().get("productId");
    public static final String SECRET_KEY = ConfigHandle.instance().get("secretKey");
    public static final String KEY_BALANCE_SERVICE = "balance";
    public static final String KEY_PROMO_SERVICE = "promo";
    public static final String KEY_HOLD_CASH_SERVICE = "holdCash";
    public static final String KEY_RELEASE_CASH_SERVICE = "releaseCash";
    public static final String KEY_PAYOUT_CASH_SERVICE = "payOutCash";
    public static final String KEY_GET_HOLD_ID_SERVICE = "getHoldId";
    public static final String KEY_GET_HOLD_STATE_SERVICE = "holdState";
    public static final String KEY_PURCHASE_SERVICE = "purchase";
    public static final String SUFFIX_HOLD_ID = "_holdId";
    public static final String HOLD_ID_DEFAULT = "0000000000000000";
    public static final boolean ENABLE_PAYMENT = ConfigHandle.instance().get("enable_payment") == null ? false : ConfigHandle.instance().get("enable_payment").equals("1");
    public static final String PREFIX_MYPLAY_PROFILE = "myplay_";
    public static final String CACHE_SERVER_BIND_PORT = "cache_binded_port_";
    public static final boolean ENABLE_DEADLOCK_DETECTOR = ConfigHandle.instance().get("enable_deadlock_detector") == null ? false : ConfigHandle.instance().get("enable_deadlock_detector").equals("1");
    public static final int DEADLOCK_TIME = ConfigHandle.instance().get("deadlock_time") == null ? 60000 : ConfigHandle.instance().getLong("deadlock_time").intValue();
    public static final String SERVER_ADDRESS_LOGIN_CACHED = "server_address_login_cached";
    public static final String SERVER_ADDRESS_CACHED = "server_address_cached";
    public static final String SEPERATOR = "|";
}

