/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.protocol;

import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineWriter;
import bitzero.engine.data.IPacket;
import bitzero.engine.data.Packet;
import bitzero.engine.data.TransportType;
import bitzero.engine.io.AbstractIOHandler;
import bitzero.engine.io.IOHandler;
import bitzero.engine.io.IProtocolCodec;
import bitzero.engine.io.protocols.ProtocolType;
import bitzero.engine.sessions.ISession;
import bitzero.server.BitZeroServer;
import bitzero.server.exceptions.ExceptionMessageComposer;
import bitzero.server.protocol.BZProtocolCodec;
import bitzero.server.protocol.binary.BinaryIoHandler;
import bitzero.server.protocol.binary.PacketHeader;
import bitzero.server.protocol.binary.PacketReadState;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZIoHandler
extends AbstractIOHandler {
    private static final String FLASH_CROSSDOMAIN_POLICY_REQ = "<policy-file-request/>";
    private static final char TAG_TOKEN = '<';
    private static final int CROSSDOMAIN_REQ_LEN = "<policy-file-request/>".length() + 1;
    private static final int UDP_PACKET_MIN_SIZE = 13;
    private static final String KEY_UDP_HANDSHAKE = "h";
    private final BinaryIoHandler binHandler;
    private final Logger logger;
    private final BitZeroServer bz;
    private final BitZeroEngine engine;
    private ByteBuffer bufferedXmlSocketPolicy;

    public BZIoHandler() {
        this.binHandler = new BinaryIoHandler(this);
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.bz = BitZeroServer.getInstance();
        this.engine = BitZeroEngine.getInstance();
        this.bufferedXmlSocketPolicy = null;
        this.setCodec(new BZProtocolCodec(this));
    }

    @Override
    public void onDataRead(ISession session, byte[] data) {
        if (data == null || data.length < 1) {
            throw new IllegalArgumentException("Unexpected null or empty byte array!");
        }
        ProtocolType sessionProtocol = (ProtocolType)((Object)session.getSystemProperty("session_protocol"));
        if (sessionProtocol == null) {
            if (data[0] == 60) {
                this.handlSocketPolicyRequest(data, session);
                return;
            }
            session.setSystemProperty("session_protocol", (Object)ProtocolType.BINARY);
            session.setSystemProperty("read_state", (Object)PacketReadState.WAIT_NEW_PACKET);
        }
        this.binHandler.handleRead(session, data);
    }

    @Override
    public void setCodec(IProtocolCodec codec) {
        super.setCodec(codec);
        this.binHandler.setProtocolCodec(codec);
    }

    @Override
    public long getReadPackets() {
        return this.binHandler.getReadPackets();
    }

    @Override
    public long getIncomingDroppedPackets() {
        return this.binHandler.getIncomingDroppedPackets();
    }

    @Override
    public void onDataRead(DatagramChannel channel, SocketAddress address, byte[] data) {
    }

    @Override
    public void onDataWrite(IPacket packet) {
        if (packet.getRecipients().size() > 0) {
            try {
                this.binHandler.handleWrite(packet);
            }
            catch (Exception e) {
                ExceptionMessageComposer composer = new ExceptionMessageComposer(e);
                this.logger.warn(composer.toString());
            }
        }
    }

    public PacketHeader decodeFirstHeaderByte(byte headerByte) {
        return new PacketHeader((headerByte & 128) > 0, (headerByte & 64) > 0, (headerByte & 32) > 0, (headerByte & 16) > 0, (headerByte & 8) > 0);
    }

    public byte encodeFirstHeaderByte(PacketHeader packetHeader) {
        byte headerByte = 0;
        if (packetHeader.isBinary()) {
            headerByte = (byte)(headerByte + 128);
        }
        if (packetHeader.isEncrypted()) {
            headerByte = (byte)(headerByte + 64);
        }
        if (packetHeader.isCompressed()) {
            headerByte = (byte)(headerByte + 32);
        }
        if (packetHeader.isBlueBoxed()) {
            headerByte = (byte)(headerByte + 16);
        }
        if (packetHeader.isBigSized()) {
            headerByte = (byte)(headerByte + 8);
        }
        return headerByte;
    }

    private byte[] handlSocketPolicyRequest(byte[] data, ISession session) {
        String stringMsg = new String(data);
        byte[] newData = data;
        if (stringMsg.startsWith("<policy-file-request/>")) {
            this.logger.debug("Handling Flash Policy request");
            if (data.length > CROSSDOMAIN_REQ_LEN) {
                newData = new byte[data.length - CROSSDOMAIN_REQ_LEN];
            }
            System.arraycopy(data, CROSSDOMAIN_REQ_LEN, newData, 0, newData.length - CROSSDOMAIN_REQ_LEN);
        }
        if (this.bufferedXmlSocketPolicy == null) {
            String policyText = this.engine.getConfiguration().getFlashCrossdomainPolicyXml();
            this.bufferedXmlSocketPolicy = ByteBuffer.allocate(policyText.length() + 1);
            this.bufferedXmlSocketPolicy.put(policyText.getBytes());
            this.bufferedXmlSocketPolicy.put((byte)0);
            this.bufferedXmlSocketPolicy.flip();
        }
        Packet policyPacket = new Packet();
        policyPacket.setData(this.bufferedXmlSocketPolicy.array());
        policyPacket.setTransportType(TransportType.TCP);
        policyPacket.setRecipients(Arrays.asList(session));
        this.engine.getEngineWriter().enqueuePacket(policyPacket);
        return newData;
    }

    private void sendUDPHandshakeResponse(ISession recipient) {
    }
}

