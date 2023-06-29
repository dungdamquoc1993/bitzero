/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  net.spy.memcached.CachedData
 *  net.spy.memcached.compat.log.Logger
 *  net.spy.memcached.transcoders.BaseSerializingTranscoder
 *  net.spy.memcached.transcoders.Transcoder
 *  net.spy.memcached.transcoders.TranscoderUtils
 */
package bitzero.util.hotfix;

import java.util.Date;
import net.spy.memcached.CachedData;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.transcoders.BaseSerializingTranscoder;
import net.spy.memcached.transcoders.Transcoder;
import net.spy.memcached.transcoders.TranscoderUtils;

public class MinhVVTranscoder
extends BaseSerializingTranscoder
implements Transcoder<Object> {
    static final int SERIALIZED = 1;
    static final int COMPRESSED = 2;
    private static final int SPECIAL_MASK = 65280;
    static final int SPECIAL_BOOLEAN = 256;
    static final int SPECIAL_INT = 512;
    static final int SPECIAL_LONG = 768;
    static final int SPECIAL_DATE = 1024;
    static final int SPECIAL_BYTE = 1280;
    static final int SPECIAL_FLOAT = 1536;
    static final int SPECIAL_DOUBLE = 1792;
    static final int SPECIAL_BYTEARRAY = 2048;
    private final TranscoderUtils tu = new TranscoderUtils(true);

    public MinhVVTranscoder() {
        this(20971520);
    }

    public MinhVVTranscoder(int max) {
        super(max);
    }

    public boolean asyncDecode(CachedData d) {
        if ((d.getFlags() & 2) != 0 || (d.getFlags() & 1) != 0) {
            return true;
        }
        return super.asyncDecode(d);
    }

    public Object decode(CachedData d) {
        byte[] data = d.getData();
        Object rv = null;
        if (d.getFlags() == 16777216) {
            d = new CachedData(1, data, this.getMaxSize());
        }
        if ((d.getFlags() & 2) != 0) {
            data = this.decompress(d.getData());
        }
        int flags = d.getFlags() & 65280;
        if ((d.getFlags() & 1) != 0 && data != null) {
            rv = this.deserialize(data);
        } else if (flags != 0 && data != null) {
            switch (flags) {
                case 256: {
                    rv = this.tu.decodeBoolean(data);
                    break;
                }
                case 512: {
                    rv = this.tu.decodeInt(data);
                    break;
                }
                case 768: {
                    rv = this.tu.decodeLong(data);
                    break;
                }
                case 1024: {
                    rv = new Date(this.tu.decodeLong(data));
                    break;
                }
                case 1280: {
                    rv = Byte.valueOf(this.tu.decodeByte(data));
                    break;
                }
                case 1536: {
                    rv = new Float(Float.intBitsToFloat(this.tu.decodeInt(data)));
                    break;
                }
                case 1792: {
                    rv = new Double(Double.longBitsToDouble(this.tu.decodeLong(data)));
                    break;
                }
                case 2048: {
                    rv = data;
                    break;
                }
                default: {
                    this.getLogger().warn("Undecodeable with flags %x", new Object[]{flags});
                    break;
                }
            }
        } else {
            rv = this.decodeString(data);
        }
        return rv;
    }

    public CachedData encode(Object o) {
        byte[] b = null;
        int flags = 0;
        if (o instanceof String) {
            b = this.encodeString((String)o);
        } else if (o instanceof Long) {
            b = this.tu.encodeLong(((Long)o).longValue());
            flags |= 768;
        } else if (o instanceof Integer) {
            b = this.tu.encodeInt(((Integer)o).intValue());
            flags |= 512;
        } else if (o instanceof Boolean) {
            b = this.tu.encodeBoolean(((Boolean)o).booleanValue());
            flags |= 256;
        } else if (o instanceof Date) {
            b = this.tu.encodeLong(((Date)o).getTime());
            flags |= 1024;
        } else if (o instanceof Byte) {
            b = this.tu.encodeByte(((Byte)o).byteValue());
            flags |= 1280;
        } else if (o instanceof Float) {
            b = this.tu.encodeInt(Float.floatToRawIntBits(((Float)o).floatValue()));
            flags |= 1536;
        } else if (o instanceof Double) {
            b = this.tu.encodeLong(Double.doubleToRawLongBits((Double)o));
            flags |= 1792;
        } else if (o instanceof byte[]) {
            b = (byte[])o;
            flags |= 2048;
        } else {
            b = this.serialize(o);
            flags |= 1;
        }
        assert (b != null);
        if (b.length > this.compressionThreshold) {
            byte[] compressed = this.compress(b);
            if (compressed.length < b.length) {
                this.getLogger().debug("Compressed %s from %d to %d", new Object[]{o.getClass().getName(), b.length, compressed.length});
                b = compressed;
                flags |= 2;
            } else {
                this.getLogger().info("Compression increased the size of %s from %d to %d", new Object[]{o.getClass().getName(), b.length, compressed.length});
            }
        }
        return new CachedData(flags, b, this.getMaxSize());
    }
}

