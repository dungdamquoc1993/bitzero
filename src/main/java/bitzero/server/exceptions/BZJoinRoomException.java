/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZException;

public class BZJoinRoomException
extends BZException {
    private static final long serialVersionUID = 6384101728401558209L;

    public BZJoinRoomException() {
    }

    public BZJoinRoomException(String message) {
        super(message);
    }

    public BZJoinRoomException(String message, BZErrorData data) {
        super(message, data);
    }
}

