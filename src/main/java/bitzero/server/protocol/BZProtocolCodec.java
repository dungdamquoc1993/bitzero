/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.server.protocol;

import bitzero.engine.data.IPacket;
import bitzero.engine.data.Packet;
import bitzero.engine.data.TransportType;
import bitzero.engine.io.IOHandler;
import bitzero.engine.io.IRequest;
import bitzero.engine.io.IResponse;
import bitzero.engine.io.Request;
import bitzero.engine.io.protocols.AbstractProtocolCodec;
import bitzero.engine.sessions.ISession;
import bitzero.server.controllers.SystemRequest;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BZProtocolCodec
extends AbstractProtocolCodec {
    private static final String CONTROLLER_ID = "c";
    private static final String ACTION_ID = "a";
    private static final String PARAM_ID = "p";
    private final AtomicLong udpPacketCounter = new AtomicLong();

    public BZProtocolCodec(IOHandler ioHandler) {
        this.setIOHandler(ioHandler);
    }

    @Override
    public void onPacketRead(IPacket packet) {
        if (packet == null) {
            throw new IllegalStateException("Protocol Codec didn't expect a null packet!");
        }
        ByteBuffer requestObject = null;
        if (packet.isTcp()) {
            ByteBuffer buff = (ByteBuffer)packet.getData();
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

    private void dispatchRequest(ByteBuffer requestObject, IPacket packet) {
        if (requestObject.capacity() < 3) {
            throw new IllegalStateException("Request rejected: No Controller ID in request!");
        }
        Request request = new Request();
        Byte controllerKey = null;
        controllerKey = Byte.valueOf(requestObject.get());
        request.setId(requestObject.getShort());
        request.setContent(requestObject.compact());
        request.setSender(packet.getSender());
        request.setTransportType(packet.getTransportType());
        this.dispatchRequestToController(request, controllerKey);
    }

    @Override
    public void onPacketWrite(IResponse response) {
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
        if (response.getRecipients().size() > 0) {
            //this.logger.debug("{OUT}: " + (Object)((Object)SystemRequest.fromId(response.getId())) + new StringBuilder().append(" - ").append(response.getId()).toString());
        }
        if (response.getRecipients().size() > 0) {
            //LoggerFactory.getLogger((String)"request").debug(" {OUT} " + response.getId() + new StringBuilder().append(" - to : ").append(response.getRecipients().size()).toString());
        }
        this.ioHandler.onDataWrite(packet);
    }
}

