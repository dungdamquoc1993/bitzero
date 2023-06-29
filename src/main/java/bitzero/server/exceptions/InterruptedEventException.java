/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.exceptions;

import bitzero.server.exceptions.BZRuntimeException;

public final class InterruptedEventException
extends BZRuntimeException {
    private static final long serialVersionUID = 1729674312557697005L;

    public InterruptedEventException() {
        super("Event Interrupted");
    }
}

