/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.util;

import bitzero.engine.data.BufferType;
import java.nio.ByteBuffer;

public class NetworkServices {
    public static ByteBuffer allocateBuffer(int size, BufferType type) {
        ByteBuffer bb = null;
        if (type == BufferType.DIRECT) {
            bb = ByteBuffer.allocateDirect(size);
        } else if (type == BufferType.HEAP) {
            bb = ByteBuffer.allocate(size);
        }
        return bb;
    }
}

