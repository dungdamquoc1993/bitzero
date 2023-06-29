/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.jboss.netty.buffer.ChannelBuffer
 *  org.jboss.netty.buffer.ChannelBuffers
 *  org.slf4j.Logger
 */
package bitzero.engine.websocket;

import bitzero.engine.data.IPacket;
import bitzero.engine.data.Packet;
import bitzero.engine.data.TransportType;
import bitzero.engine.io.IOHandler;
import bitzero.engine.io.IRequest;
import bitzero.engine.io.IResponse;
import bitzero.engine.io.Request;
import bitzero.engine.io.protocols.AbstractProtocolCodec;
import bitzero.engine.sessions.ISession;
import bitzero.engine.util.ByteUtils;
import bitzero.engine.websocket.IWebSocketChannel;
import bitzero.engine.websocket.WebSocketStats;
import bitzero.server.BitZeroServer;
import bitzero.server.controllers.SystemRequest;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collection;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;

public class WebSocketProtocolCodec
extends AbstractProtocolCodec {
    private static final String CONTROLLER_ID = "c";
    private static final String ACTION_ID = "a";
    private static final String PARAM_ID = "p";
    private final WebSocketStats webSocketStats;

    public WebSocketProtocolCodec(WebSocketStats wss) {
        this.webSocketStats = wss;
    }

    @Override
    public void onPacketRead(IPacket packet) {
        if (packet == null) {
            throw new IllegalStateException("Protocol Codec didn't expect a null packet!");
        }
        ByteBuffer requestObject = null;
        if (packet.isTcp()) {
            ByteBuffer buff = (ByteBuffer)packet.getData();
            byte[] rawPacket = buff.array();
            if (rawPacket.length < 1024 && BitZeroServer.isDebug()) {
                this.logger.debug(ByteUtils.fullHexDump(rawPacket));
            }
            try {
                requestObject = buff;
                requestObject.rewind();
            }
            catch (Exception e) {
                this.logger.warn("Error deserializing request: " + e);
            }
        } else if (packet.isUdp()) {
            requestObject = (ByteBuffer)packet.getData();
        }
        if (requestObject != null) {
            this.logger.debug(requestObject.toString());
            this.dispatchRequest(requestObject, packet);
        }
    }

    @Override
    public void onPacketWrite(IResponse response) {
        byte[] rawPacket;
        byte[] binData = (byte[])response.getContent();
        ByteBuffer packetBuffer = ByteBuffer.allocate(3 + binData.length);
        packetBuffer.put(((Byte)response.getTargetController()).byteValue());
        packetBuffer.putShort((Short)response.getId());
        packetBuffer.put(binData);
        Packet packet = new Packet();
        packet.setId((Short)response.getId());
        packet.setTransportType(response.getTransportType());
        packet.setData(packetBuffer.array());
        packet.setRecipients(response.getRecipients());
        if (response.getRecipients().size() > 0 && this.logger.isDebugEnabled()) {
            //this.logger.debug("{OUT}: " + (Object)((Object)SystemRequest.fromId(response.getId())));
        }
        if ((rawPacket = (byte[])packet.getData()).length < 1024 && BitZeroServer.isDebug()) {
            //this.logger.debug(ByteUtils.fullHexDump(rawPacket));
        }
        ChannelBuffer cb = ChannelBuffers.wrappedBuffer((byte[])rawPacket);
        for (Object tmp : response.getRecipients()) {
            ISession session = (ISession)tmp;
            IWebSocketChannel channel = (IWebSocketChannel)session.getSystemProperty("wsChannel");
            channel.write(cb);
            session.addWrittenBytes(rawPacket.length);
            int bytesLen = rawPacket.length;
            this.webSocketStats.addWrittenPackets(1);
            this.webSocketStats.addWrittenBytes(bytesLen);
        }
    }

    private void dispatchRequest(ByteBuffer requestObject, IPacket packet) {
        if (requestObject.capacity() < 3) {
            throw new IllegalStateException("Request rejected: No Controller ID in request!");
        }
        Request request = new Request();
        Byte controllerKey = null;
        requestObject.get();
        requestObject.getShort();
        controllerKey = Byte.valueOf(requestObject.get());
        request.setId(requestObject.getShort());
        request.setContent(requestObject.compact());
        request.setSender(packet.getSender());
        request.setTransportType(packet.getTransportType());
        this.dispatchRequestToController(request, controllerKey);
    }

    @Override
    public IOHandler getIOHandler() {
        throw new UnsupportedOperationException("Now Allowed!");
    }

    @Override
    public void setIOHandler(IOHandler handler) {
        throw new UnsupportedOperationException("Now Allowed!");
    }
}

