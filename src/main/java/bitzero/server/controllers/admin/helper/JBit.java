/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package bitzero.server.controllers.admin.helper;

import java.lang.reflect.Field;
import org.json.JSONObject;

public class JBit {
    private Object runObject(Object obj, String fname) {
        try {
            Field f = obj.getClass().getDeclaredField(fname);
            f.setAccessible(true);
            return f.get(obj);
        }
        catch (Exception e) {
            return null;
        }
    }

    public /* varargs */ Object runObject(Object obj, String ... fnames) {
        if (obj == null || fnames.length == 0) {
            return null;
        }
        Object nextObj = this.runObject(obj, fnames[0]);
        for (int i = 1; i < fnames.length; ++i) {
            nextObj = this.runObject(nextObj, fnames[i]);
        }
        return nextObj;
    }

    public String revertObject(Object obj) {
        JSONObject json = new JSONObject();
        Field[] fs = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fs.length; ++i) {
            try {
                Field f = fs[i];
                f.setAccessible(true);
                if (f.getType().isPrimitive() || f.getType() == String.class) {
                    json.put(f.getName(), f.get(obj));
                    continue;
                }
                json.put(f.getName(), (Object)"Object");
                continue;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        return json.toString();
    }
}

