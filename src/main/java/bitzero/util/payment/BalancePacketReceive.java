/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package bitzero.util.payment;

import bitzero.util.payment.PacketPaymentReceive;
import org.json.JSONObject;

public class BalancePacketReceive
extends PacketPaymentReceive {
    public String AccountName;
    public long CashRemain;
    public int RetCode;

    public BalancePacketReceive(JSONObject data) {
        super(data);
    }
}

