/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZException;

public class BZFloodingException
extends BZException {
    public BZFloodingException() {
    }

    public BZFloodingException(String message) {
        super(message);
    }

    public BZFloodingException(String message, BZErrorData data) {
        super(message, data);
    }
}

