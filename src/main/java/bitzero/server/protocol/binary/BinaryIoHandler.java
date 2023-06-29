/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.protocol.binary;

import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineWriter;
import bitzero.engine.data.IPacket;
import bitzero.engine.data.Packet;
import bitzero.engine.data.TransportType;
import bitzero.engine.io.IProtocolCodec;
import bitzero.engine.io.protocols.ProtocolType;
import bitzero.engine.sessions.ISession;
import bitzero.engine.util.ByteUtils;
import bitzero.server.BitZeroServer;
import bitzero.server.api.APIManager;
import bitzero.server.api.IBZApi;
import bitzero.server.config.IConfigurator;
import bitzero.server.config.ServerSettings;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.exceptions.ExceptionMessageComposer;
import bitzero.server.protocol.BZIoHandler;
import bitzero.server.protocol.binary.DefaultPacketCompressor;
import bitzero.server.protocol.binary.DefaultPacketEncrypter;
import bitzero.server.protocol.binary.IPacketCompressor;
import bitzero.server.protocol.binary.IPacketEncrypter;
import bitzero.server.protocol.binary.PacketHeader;
import bitzero.server.protocol.binary.PacketReadState;
import bitzero.server.protocol.binary.PendingPacket;
import bitzero.server.protocol.binary.ProcessedPacket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryIoHandler {
    private static final int SHORT_SIZE = 2;
    private static final int INT_SIZE = 4;
    private final Logger log;
    private final BitZeroEngine engine;
    private final BitZeroServer bz;
    private final BZIoHandler parentHandler;
    private final IPacketCompressor packetCompressor;
    private final IPacketEncrypter packetEncrypter;
    private IProtocolCodec protocolCodec;
    private ServerSettings serverSettings;
    private final int maxIncomingPacketSize;
    private volatile long packetsRead;
    private volatile long droppedIncomingPackets;

    public BinaryIoHandler(BZIoHandler parentHandler) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.engine = BitZeroEngine.getInstance();
        this.bz = BitZeroServer.getInstance();
        this.packetCompressor = new DefaultPacketCompressor();
        this.packetEncrypter = new DefaultPacketEncrypter();
        this.maxIncomingPacketSize = BitZeroEngine.getInstance().getConfiguration().getMaxIncomingRequestSize();
        this.packetsRead = 0;
        this.droppedIncomingPackets = 0;
        this.parentHandler = parentHandler;
        this.serverSettings = BitZeroServer.getInstance().getConfigurator().getServerSettings();
    }

    public void setProtocolCodec(IProtocolCodec protocolCodec) {
        this.protocolCodec = protocolCodec;
    }

    public IPacketCompressor getPacketCompressor() {
        return this.packetCompressor;
    }

    public long getReadPackets() {
        return this.packetsRead;
    }

    public long getIncomingDroppedPackets() {
        return this.droppedIncomingPackets;
    }

    public void handleWrite(IPacket packet) throws Exception {
        byte[] binData = (byte[])packet.getData();
        packet.setData(binData);
        boolean isEncrypted = false;
        if (packet.getAttribute("encryption") != null) {
            isEncrypted = (Boolean)packet.getAttribute("encryption");
        }
        boolean isCompressed = false;
        int originalSize = binData.length;
        if (!isEncrypted && binData.length > this.serverSettings.protocolCompressionThreshold) {
            byte[] beforeCompression = binData;
            if ((binData = this.packetCompressor.compress(binData)) != beforeCompression) {
                isCompressed = true;
            }
        }
        int sizeBytes = 2;
        if (binData.length > 65535) {
            sizeBytes = 4;
        }
        PacketHeader packetHeader = new PacketHeader(true, isEncrypted, isCompressed, false, sizeBytes > 2);
        byte headerByte = this.parentHandler.encodeFirstHeaderByte(packetHeader);
        ByteBuffer packetBuffer = ByteBuffer.allocate(1 + sizeBytes + binData.length);
        packetBuffer.put(headerByte);
        if (sizeBytes > 2) {
            packetBuffer.putInt(binData.length);
        } else {
            packetBuffer.putShort((short)binData.length);
        }
        packetBuffer.put(binData);
        packet.setData(packetBuffer.array());
        if (isCompressed && this.log.isDebugEnabled()) {
            this.log.debug(String.format(" (cmp: %sb / %sb)", originalSize, binData.length));
        }
        if (binData.length < 1024 && BitZeroServer.isDebug()) {
            this.log.debug(ByteUtils.fullHexDump((byte[])packet.getData()));
        }
        this.engine.getEngineWriter().enqueuePacket(packet);
    }

    public void handleRead(ISession session, byte[] data) {
        if (data.length < 1024 && BitZeroServer.isDebug()) {
            this.log.debug(ByteUtils.fullHexDump(data));
        }
        PacketReadState readState = (PacketReadState)((Object)session.getSystemProperty("read_state"));
        try {
            while (data.length > 0) {
                ProcessedPacket process;
                this.log.debug("STATE: " + (Object)((Object)readState));
                if (readState == PacketReadState.WAIT_NEW_PACKET) {
                    process = this.handleNewPacket(session, data);
                    readState = process.getState();
                    data = process.getData();
                }
                if (readState == PacketReadState.WAIT_DATA_SIZE) {
                    process = this.handleDataSize(session, data);
                    readState = process.getState();
                    data = process.getData();
                }
                if (readState == PacketReadState.WAIT_DATA_SIZE_FRAGMENT) {
                    process = this.handleDataSizeFragment(session, data);
                    readState = process.getState();
                    data = process.getData();
                }
                if (readState != PacketReadState.WAIT_DATA) continue;
                process = this.handlePacketData(session, data);
                readState = process.getState();
                data = process.getData();
            }
        }
        catch (Exception err) {
            ExceptionMessageComposer emc = new ExceptionMessageComposer(err);
            this.log.warn(emc.toString());
            readState = PacketReadState.WAIT_NEW_PACKET;
        }
        session.setSystemProperty("read_state", (Object)readState);
    }

    private ProcessedPacket handleNewPacket(ISession session, byte[] data) {
        PacketHeader header = this.parentHandler.decodeFirstHeaderByte(data[0]);
        session.setSystemProperty("session_data_buffer", new PendingPacket(header));
        data = ByteUtils.resizeByteArray(data, 1, data.length - 1);
        return new ProcessedPacket(PacketReadState.WAIT_DATA_SIZE, data);
    }

    private ProcessedPacket handleDataSize(ISession session, byte[] data) {
        PacketReadState state = PacketReadState.WAIT_DATA;
        PendingPacket pending = (PendingPacket)session.getSystemProperty("session_data_buffer");
        int dataSize = -1;
        int sizeBytes = 2;
        if (pending.getHeader().isBigSized()) {
            if (data.length >= 4) {
                dataSize = 0;
                for (int i = 0; i < 4; ++i) {
                    int pow256 = (int)Math.pow(256.0, 3 - i);
                    int intByte = data[i] & 255;
                    dataSize += pow256 * intByte;
                }
            }
            sizeBytes = 4;
            this.log.debug("BIG SIZED PACKET: " + (dataSize != -1 ? Integer.valueOf(dataSize) : "Unknown"));
        } else {
            if (data.length >= 2) {
                int msb = data[0] & 255;
                int lsb = data[1] & 255;
                dataSize = msb * 256 + lsb;
            }
            this.log.debug("NORMAL SIZED PACKET: " + (dataSize != -1 ? Integer.valueOf(dataSize) : "Unknown"));
        }
        if (dataSize != -1) {
            this.validateIncomingDataSize(session, dataSize);
            pending.getHeader().setExpectedLen(dataSize);
            pending.setBuffer(ByteBuffer.allocate(dataSize));
            data = ByteUtils.resizeByteArray(data, sizeBytes, data.length - sizeBytes);
        } else {
            state = PacketReadState.WAIT_DATA_SIZE_FRAGMENT;
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
            sizeBuffer.put(data);
            pending.setBuffer(sizeBuffer);
            data = new byte[]{};
        }
        return new ProcessedPacket(state, data);
    }

    private ProcessedPacket handleDataSizeFragment(ISession session, byte[] data) {
        int remaining;
        this.log.debug("Handling DataSize fragment...");
        PacketReadState state = PacketReadState.WAIT_DATA_SIZE_FRAGMENT;
        PendingPacket pending = (PendingPacket)session.getSystemProperty("session_data_buffer");
        ByteBuffer sizeBuffer = (ByteBuffer)pending.getBuffer();
        int n = remaining = pending.getHeader().isBigSized() ? 4 - sizeBuffer.position() : 2 - sizeBuffer.position();
        if (data.length >= remaining) {
            sizeBuffer.put(data, 0, remaining);
            sizeBuffer.flip();
            int dataSize = pending.getHeader().isBigSized() ? sizeBuffer.getInt() : (int)sizeBuffer.getShort();
            this.log.debug("DataSize is ready: " + dataSize);
            this.validateIncomingDataSize(session, dataSize);
            pending.getHeader().setExpectedLen(dataSize);
            pending.setBuffer(ByteBuffer.allocate(dataSize));
            state = PacketReadState.WAIT_DATA;
            data = data.length > remaining ? ByteUtils.resizeByteArray(data, remaining, data.length - remaining) : new byte[]{};
        } else {
            sizeBuffer.put(data);
            data = new byte[]{};
        }
        return new ProcessedPacket(state, data);
    }

    private ProcessedPacket handlePacketData(ISession session, byte[] data) throws Exception {
        boolean isThereMore;
        PacketReadState state = PacketReadState.WAIT_DATA;
        PendingPacket pending = (PendingPacket)session.getSystemProperty("session_data_buffer");
        ByteBuffer dataBuffer = (ByteBuffer)pending.getBuffer();
        int readLen = dataBuffer.remaining();
        boolean bl = isThereMore = data.length > readLen;
        if (data.length >= readLen) {
            dataBuffer.put(data, 0, readLen);
            if (pending.getHeader().getExpectedLen() != dataBuffer.capacity()) {
                throw new IllegalStateException("Expected data size differs from the buffer capacity! Expected: " + pending.getHeader().getExpectedLen() + ", Buffer size: " + dataBuffer.capacity());
            }
            this.log.debug("<<< PACKET COMPLETE >>>");
            if (pending.getHeader().isCompressed()) {
                byte[] compressedData;
                if (data.length == readLen) {
                    compressedData = dataBuffer.array();
                } else {
                    compressedData = new byte[pending.getHeader().getExpectedLen()];
                    System.arraycopy(dataBuffer.array(), 0, compressedData, 0, compressedData.length);
                }
                long t1 = System.nanoTime();
                byte[] deflatedData = this.packetCompressor.uncompress(compressedData);
                long t2 = System.nanoTime();
                this.log.debug("<<< Packet was decompressed >>>");
                this.log.debug(String.format("Original: %s, Deflated: %s, Comp. Ratio: %s%%, Time: %sms.", dataBuffer.capacity(), deflatedData.length, 100 - dataBuffer.capacity() * 100 / deflatedData.length, Float.valueOf((float)(t2 - t1) / 1000000.0f)));
                dataBuffer = ByteBuffer.wrap(deflatedData);
            }
            Packet newPacket = new Packet();
            newPacket.setData(dataBuffer);
            newPacket.setSender(session);
            newPacket.setOriginalSize(dataBuffer.capacity());
            newPacket.setTransportType(TransportType.TCP);
            newPacket.setAttribute("type", (Object)ProtocolType.BINARY);
            ++this.packetsRead;
            this.protocolCodec.onPacketRead(newPacket);
            state = PacketReadState.WAIT_NEW_PACKET;
        } else {
            dataBuffer.put(data);
            this.log.debug("NOT ENOUGH DATA, GO AHEAD");
        }
        data = isThereMore ? ByteUtils.resizeByteArray(data, readLen, data.length - readLen) : new byte[]{};
        return new ProcessedPacket(state, data);
    }

    private void validateIncomingDataSize(ISession session, int dataSize) {
        String who;
        User user = this.bz.getUserManager().getUserBySession(session);
        String string = who = user == null ? session.toString() : user.toString();
        if (dataSize < 1) {
            ++this.droppedIncomingPackets;
            throw new IllegalArgumentException("Illegal request size: " + dataSize + " bytes, from: " + who);
        }
        if (dataSize > this.maxIncomingPacketSize) {
            this.bz.getAPIManager().getBzApi().disconnect(session);
            ++this.droppedIncomingPackets;
            throw new IllegalArgumentException(String.format("Incoming request size too large: %s, Current limit: %s, From: %s", dataSize, this.maxIncomingPacketSize, who));
        }
    }
}

