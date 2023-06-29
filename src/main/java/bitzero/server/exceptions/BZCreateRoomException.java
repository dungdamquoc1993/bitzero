/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZException;

public class BZCreateRoomException
extends BZException {
    private static final long serialVersionUID = 4751733417642191809L;

    public BZCreateRoomException() {
    }

    public BZCreateRoomException(String message) {
        super(message);
    }

    public BZCreateRoomException(String message, BZErrorData data) {
        super(message, data);
    }
}

