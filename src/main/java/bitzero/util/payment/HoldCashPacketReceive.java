/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package bitzero.util.payment;

import bitzero.util.payment.PacketPaymentReceive;
import org.json.JSONObject;

public class HoldCashPacketReceive
extends PacketPaymentReceive {
    public String HoldID;
    public int UserCnt;
    public int RetCode;

    public HoldCashPacketReceive(JSONObject data) {
        super(data);
    }
}

