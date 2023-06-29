/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol.binary;

public interface IPacketCompressor {
    public byte[] compress(byte[] var1) throws Exception;

    public byte[] uncompress(byte[] var1) throws Exception;
}

