/*
 * Decompiled with CFR 0_116.
 */
package bitzero.util.payment;

public class ItemInfo {
    public int id;
    public String name;
    public int quantity;

    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.id);
        sBuilder.append(":");
        sBuilder.append(this.name);
        sBuilder.append(":");
        sBuilder.append(this.quantity);
        return sBuilder.toString();
    }
}

