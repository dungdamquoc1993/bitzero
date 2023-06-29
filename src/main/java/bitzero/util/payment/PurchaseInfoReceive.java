/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package bitzero.util.payment;

import bitzero.util.payment.PacketPaymentReceive;
import org.json.JSONObject;

public class PurchaseInfoReceive
extends PacketPaymentReceive {
    public String PurchaseID;
    public String AccountName;
    public long ItemID;
    public int ItemQuantity;
    public String ItemName;
    public int CashAmt;
    public String PurchaseCode;
    public int Reserved;
    public long CashRemain;
    public int RetCode;

    public PurchaseInfoReceive(JSONObject data) {
        super(data);
    }
}

