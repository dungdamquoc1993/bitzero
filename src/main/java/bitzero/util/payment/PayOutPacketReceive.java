/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package bitzero.util.payment;

import bitzero.util.payment.PacketPaymentReceive;
import org.json.JSONArray;
import org.json.JSONObject;

public class PayOutPacketReceive
extends PacketPaymentReceive {
    public String PayOutID;
    public String HoldID;
    public String GameCode;
    public int UserCnt;
    public int RetCode;
    public JSONArray AccList;

    public PayOutPacketReceive(JSONObject data) {
        super(data);
    }
}

