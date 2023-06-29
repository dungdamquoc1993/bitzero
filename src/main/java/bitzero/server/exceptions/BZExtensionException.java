/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.BZErrorData;
import bitzero.server.exceptions.BZException;

public class BZExtensionException
extends BZException {
    public BZExtensionException() {
    }

    public BZExtensionException(String message) {
        super(message);
    }

    public BZExtensionException(String message, BZErrorData data) {
        super(message, data);
    }
}

