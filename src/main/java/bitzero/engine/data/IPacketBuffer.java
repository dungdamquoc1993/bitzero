/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.data;

public interface IPacketBuffer {
    public boolean isMultiSegment();

    public boolean hasMoreSegments();

    public int getRemaining();

    public int getPosition();

    public int getSize();

    public byte[] getSegment();

    public void setSegment(byte[] var1);

    public void setData(byte[] var1, int var2);

    public byte[] nextSegment();

    public void backward(int var1);

    public void forward(int var1);
}

