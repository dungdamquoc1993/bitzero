/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package bitzero.util.payment;

import bitzero.util.payment.PacketPaymentReceive;
import org.json.JSONObject;

public class HoldStatePacketReceive
extends PacketPaymentReceive {
    public String AccountName;
    public String HoldID;
    public int RetCode;

    public HoldStatePacketReceive(JSONObject data) {
        super(data);
    }
}

