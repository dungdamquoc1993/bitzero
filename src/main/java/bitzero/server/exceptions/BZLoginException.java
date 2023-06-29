/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZException;

public class BZLoginException
extends BZException {
    public BZLoginException() {
    }

    public BZLoginException(String message) {
        super(message);
    }

    public BZLoginException(String message, BZErrorData data) {
        super(message, data);
    }
}

