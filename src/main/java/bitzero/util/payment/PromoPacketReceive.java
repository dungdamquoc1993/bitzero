/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package bitzero.util.payment;

import bitzero.util.payment.PacketPaymentReceive;
import org.json.JSONObject;

public class PromoPacketReceive
extends PacketPaymentReceive {
    public String CashID;
    public String AccountName;
    public String AccountNumb;
    public int CashAmt;
    public String CashCode;
    public int PromoCampaignId;
    public long CashRemain;
    public int RetCode;

    public PromoPacketReceive(JSONObject data) {
        super(data);
    }
}

