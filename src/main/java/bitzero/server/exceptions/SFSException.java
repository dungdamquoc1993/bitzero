/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.SFSErrorData;

public class SFSException
extends Exception {
    private static final long serialVersionUID = 6052949605652105170L;
    SFSErrorData errorData;

    public SFSException() {
        this.errorData = null;
    }

    public SFSException(String message) {
        super(message);
        this.errorData = null;
    }

    public SFSException(String message, SFSErrorData data) {
        super(message);
        this.errorData = data;
    }

    public SFSException(Throwable t) {
        super(t);
        this.errorData = null;
    }

    public SFSErrorData getErrorData() {
        return this.errorData;
    }
}

