/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util.random;

import java.util.Random;

public class RandomAccess {
    public static int randomInt() {
        Random random = new Random();
        return random.nextInt();
    }
}

