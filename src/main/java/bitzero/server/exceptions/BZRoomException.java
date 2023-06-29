/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZException;

public class BZRoomException
extends BZException {
    public BZRoomException() {
    }

    public BZRoomException(String message) {
        super(message);
    }

    public BZRoomException(String message, BZErrorData data) {
        super(message, data);
    }
}

