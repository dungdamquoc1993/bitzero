/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package bitzero.util.payment;

import java.lang.reflect.Field;
import org.json.JSONException;
import org.json.JSONObject;

public class PacketPaymentReceive {
    public PacketPaymentReceive(JSONObject data) {
        this.parseJson(data);
    }

    public void parseJson(JSONObject data) {
        Field[] fields;
        for (Field f : fields = this.getClass().getFields()) {
            try {
                if (f.getModifiers() != 1) continue;
                f.set(this, data.get(f.getName()));
                continue;
            }
            catch (IllegalAccessException var7_8) {
                continue;
            }
            catch (JSONException var7_9) {
                // empty catch block
            }
        }
    }
}

