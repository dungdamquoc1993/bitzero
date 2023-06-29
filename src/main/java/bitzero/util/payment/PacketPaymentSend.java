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

public class PacketPaymentSend {
    public JSONObject toJSONObject() {
        Field[] fields;
        JSONObject data = new JSONObject();
        for (Field f : fields = this.getClass().getFields()) {
            try {
                if (f.getModifiers() != 1) continue;
                data.put(f.getName(), f.get(this));
                continue;
            }
            catch (IllegalAccessException var7_8) {
                continue;
            }
            catch (JSONException var7_9) {
                // empty catch block
            }
        }
        return data;
    }

    public String toString() {
        return this.toJSONObject().toString();
    }
}

