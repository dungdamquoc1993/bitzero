/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.SFSErrorData;
import bitzero.server.exceptions.SFSException;

public class SFSExtensionException
extends SFSException {
    public SFSExtensionException() {
    }

    public SFSExtensionException(String message) {
        super(message);
    }

    public SFSExtensionException(String message, SFSErrorData data) {
        super(message, data);
    }
}

