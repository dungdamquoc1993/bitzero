/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol.binary;

import bitzero.engine.sessions.ISession;

public interface IPacketEncrypter {
    public byte[] encrypt(ISession var1, byte[] var2);

    public byte[] decrypt(ISession var1, byte[] var2);
}

