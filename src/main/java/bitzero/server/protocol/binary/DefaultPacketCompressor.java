/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.protocol.binary;

import bitzero.server.protocol.binary.IPacketCompressor;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class DefaultPacketCompressor
implements IPacketCompressor {
    public final int MAX_SIZE_FOR_COMPRESSION = 1000000;
    private final int compressionBufferSize = 256;

    @Override
    public byte[] compress(byte[] data) throws Exception {
        if (data.length > 1000000) {
            return data;
        }
        Deflater compressor = new Deflater();
        compressor.setInput(data);
        compressor.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[256];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        bos.close();
        return bos.toByteArray();
    }

    @Override
    public byte[] uncompress(byte[] zipData) throws Exception {
        Inflater unzipper = new Inflater();
        unzipper.setInput(zipData);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(zipData.length);
        byte[] buf = new byte[256];
        while (!unzipper.finished()) {
            int count = unzipper.inflate(buf);
            bos.write(buf, 0, count);
        }
        bos.close();
        return bos.toByteArray();
    }
}

