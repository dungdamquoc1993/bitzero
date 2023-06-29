/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.data;

public enum MessagePriority {
    VERY_LOW(1, "Very LOW"),
    LOW(2, "LOW"),
    NORMAL(3, "NORMAL"),
    HIGH(4, "HIGH"),
    VERY_HIGH(5, "Very HIGH");
    
    private int level;
    private String repr;

    private MessagePriority(int lev, String repr) {
        this.level = lev;
        this.repr = repr;
    }

    public int getValue() {
        return this.level;
    }

    public String toString() {
        return "{ " + this.repr + " }";
    }
}

