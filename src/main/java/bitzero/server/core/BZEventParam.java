/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.core;

import bitzero.server.core.IBZEventParam;

public enum BZEventParam implements IBZEventParam
{
    ZONE,
    ROOM,
    USER,
    LOGIN_NAME,
    LOGIN_PASSWORD,
    LOGIN_IN_DATA,
    LOGIN_OUT_DATA,
    JOINED_ROOMS,
    PLAYER_ID,
    PLAYER_IDS_BY_ROOM,
    SESSION,
    DISCONNECTION_REASON,
    VARIABLES,
    RECIPIENT,
    MESSAGE,
    COMMAND,
    PAYMENT_TYPE,
    GROSS_NUM,
    NET_NUM,
    ZING_ID,
    CASH_NUM,
    PROMO_NUM,
    TRANSACTIONID;
    

    private BZEventParam() {
    }
}

