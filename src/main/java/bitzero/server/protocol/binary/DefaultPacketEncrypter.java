/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol.binary;

import bitzero.engine.sessions.ISession;
import bitzero.server.protocol.binary.IPacketEncrypter;

public class DefaultPacketEncrypter
implements IPacketEncrypter {
    @Override
    public byte[] decrypt(ISession session, byte[] data) {
        return null;
    }

    @Override
    public byte[] encrypt(ISession session, byte[] data) {
        return null;
    }
}

