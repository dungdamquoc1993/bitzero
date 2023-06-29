/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZException;

public class BZTooManyRoomsException
extends BZException {
    public BZTooManyRoomsException(String message) {
        super(message);
    }

    public BZTooManyRoomsException(String message, BZErrorData data) {
        super(message, data);
    }
}

