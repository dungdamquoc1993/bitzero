/*
 * Decompiled with CFR 0_116.
 */
package bitzero.server.util;

import bitzero.engine.util.ByteUtils;
import bitzero.server.exceptions.BZException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ByteArray {
    private byte[] buffer;
    private int position = 0;
    private boolean compressed = false;

    public byte[] getBytes() {
        return this.buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.compressed = false;
    }

    public int getLength() {
        return this.buffer.length;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getBytesAvailable() {
        int val = this.buffer.length - this.position;
        if (val > this.buffer.length || val < 0) {
            val = 0;
        }
        return val;
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public ByteArray() {
        this.buffer = new byte[0];
    }

    public ByteArray(byte[] buf) {
        this.buffer = buf;
    }

    public void compress() throws BZException {
        if (this.compressed) {
            throw new BZException("Buffer is already compressed");
        }
        try {
            Deflater compressor = new Deflater();
            compressor.setLevel(9);
            compressor.setInput(this.buffer);
            compressor.finish();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
            bos.close();
            this.buffer = bos.toByteArray();
            this.position = 0;
            this.compressed = true;
        }
        catch (IOException e) {
            throw new BZException("Error compressing data");
        }
    }

    public void uncompress() throws BZException {
        try {
            Inflater decompressor = new Inflater();
            decompressor.setInput(this.buffer);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
            bos.close();
            this.buffer = bos.toByteArray();
            this.position = 0;
            this.compressed = false;
        }
        catch (DataFormatException e) {
            throw new BZException("Data format exception decompressing buffer");
        }
        catch (IOException e) {
            throw new BZException("Error decompressing data");
        }
    }

    private void checkCompressedWrite() throws BZException {
        if (this.compressed) {
            throw new BZException("Only raw bytes can be written a compressed array. Call Uncompress first.");
        }
    }

    private void checkCompressedRead() throws BZException {
        if (this.compressed) {
            throw new BZException("Only raw bytes can be read from a compressed array.");
        }
    }

    public byte[] reverseOrder(byte[] dt) {
        return dt;
    }

    public void writeByte(int type) {
        this.writeByte((byte)type);
    }

    public void writeByte(byte b) {
        byte[] buf = new byte[]{b};
        this.writeBytes(buf);
    }

    public void writeBytes(byte[] data) {
        this.writeBytes(data, data.length);
    }

    public void writeBytes(byte[] data, int count) {
        ByteBuffer newBuffer = ByteBuffer.allocate(count + this.buffer.length);
        newBuffer.put(this.buffer);
        byte[] addBuffer = new byte[count];
        ByteBuffer.wrap(data).get(addBuffer, 0, count);
        newBuffer.put(addBuffer);
        this.buffer = newBuffer.array();
    }

    public void writeBool(boolean b) throws BZException {
        this.checkCompressedWrite();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeByte(b ? 1 : 0);
            this.writeBytes(bos.toByteArray());
        }
        catch (IOException e) {
            throw new BZException("Error writing to data buffer");
        }
    }

    public void writeInt(int i) throws BZException {
        this.checkCompressedWrite();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeInt(i);
            this.writeBytes(this.reverseOrder(bos.toByteArray()));
        }
        catch (IOException e) {
            throw new BZException("Error writing to data buffer");
        }
    }

    public void writeShort(short s) throws BZException {
        this.checkCompressedWrite();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeShort(s);
            this.writeBytes(this.reverseOrder(bos.toByteArray()));
        }
        catch (IOException e) {
            throw new BZException("Error writing to data buffer");
        }
    }

    public void writeUShort(int s) throws BZException {
        this.checkCompressedWrite();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            int b1 = (s & 65280) >> 8;
            int b2 = s & 255;
            dos.writeByte((byte)b1);
            dos.writeByte((byte)b2);
            this.writeBytes(bos.toByteArray());
        }
        catch (IOException e) {
            throw new BZException("Error writing to data buffer");
        }
    }

    public void writeLong(long l) throws BZException {
        this.checkCompressedWrite();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeLong(l);
            this.writeBytes(this.reverseOrder(bos.toByteArray()));
        }
        catch (IOException e) {
            throw new BZException("Error writing to data buffer");
        }
    }

    public void writeFloat(float f) throws BZException {
        this.checkCompressedWrite();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeFloat(f);
            this.writeBytes(this.reverseOrder(bos.toByteArray()));
        }
        catch (IOException e) {
            throw new BZException("Error writing to data buffer");
        }
    }

    public void writeDouble(double d) throws BZException {
        this.checkCompressedWrite();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeDouble(d);
            this.writeBytes(this.reverseOrder(bos.toByteArray()));
        }
        catch (IOException e) {
            throw new BZException("Error writing to data buffer");
        }
    }

    public void writeUTF(String str) throws BZException {
        this.checkCompressedWrite();
        int utfLen = 0;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c >= '\u0001' && c <= '') {
                ++utfLen;
                continue;
            }
            if (c > '\u07ff') {
                utfLen += 3;
                continue;
            }
            utfLen += 2;
        }
        if (utfLen > 32768) {
            throw new BZException("String length cannot be greater then 32768 !");
        }
        try {
            this.writeShort((short)utfLen);
            this.writeBytes(str.getBytes("UTF8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new BZException("Error writing to data buffer");
        }
    }

    public byte readByte() throws BZException {
        this.checkCompressedRead();
        return this.buffer[this.position++];
    }

    public byte[] readBytes(int count) {
        byte[] res = new byte[count];
        ByteBuffer buf = ByteBuffer.wrap(this.buffer);
        buf.position(this.position);
        buf.get(res);
        this.position += count;
        return res;
    }

    public boolean readBool() throws BZException {
        this.checkCompressedRead();
        return this.buffer[this.position++] == 1;
    }

    public int readInt() throws BZException {
        this.checkCompressedRead();
        byte[] data = this.reverseOrder(this.readBytes(4));
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try {
            return dis.readInt();
        }
        catch (IOException e) {
            throw new BZException("Error reading from data buffer");
        }
    }

    public short readShort() throws BZException {
        this.checkCompressedRead();
        byte[] data = this.reverseOrder(this.readBytes(2));
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try {
            return dis.readShort();
        }
        catch (IOException e) {
            throw new BZException("Error reading from data buffer");
        }
    }

    public int readUShort() throws BZException {
        int ib2;
        this.checkCompressedRead();
        byte[] data = this.reverseOrder(this.readBytes(2));
        int ib1 = new Integer(data[0]);
        if (ib1 < 0) {
            ib1 = data[0] & 128;
            ib1 += data[0] & 127;
        }
        if ((ib2 = new Integer(data[1]).intValue()) < 0) {
            ib2 = data[1] & 128;
            ib2 += data[1] & 127;
        }
        return ib1 * 256 + ib2;
    }

    public long readLong() throws BZException {
        this.checkCompressedRead();
        byte[] data = this.reverseOrder(this.readBytes(8));
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try {
            return dis.readLong();
        }
        catch (IOException e) {
            throw new BZException("Error reading from data buffer");
        }
    }

    public float readFloat() throws BZException {
        this.checkCompressedRead();
        byte[] data = this.reverseOrder(this.readBytes(4));
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try {
            return dis.readFloat();
        }
        catch (IOException e) {
            throw new BZException("Error reading from data buffer");
        }
    }

    public double readDouble() throws BZException {
        this.checkCompressedRead();
        byte[] data = this.reverseOrder(this.readBytes(8));
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try {
            return dis.readDouble();
        }
        catch (IOException e) {
            throw new BZException("Error reading from data buffer");
        }
    }

    public String readUTF() throws BZException {
        try {
            this.checkCompressedRead();
            short size = this.readShort();
            byte[] data = this.readBytes(size);
            return new String(data, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new BZException("Error reading from data buffer");
        }
    }

    public String getDump() {
        return ByteUtils.fullHexDump(this.buffer);
    }

    public String getHexDump() {
        if (this.buffer == null || this.buffer.length <= 0) {
            return "";
        }
        return ByteUtils.fullHexDump(this.buffer);
    }
}

