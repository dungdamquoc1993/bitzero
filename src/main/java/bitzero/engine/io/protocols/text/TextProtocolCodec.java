/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.io.protocols.text;

import bitzero.engine.data.IPacket;
import bitzero.engine.data.Packet;
import bitzero.engine.io.IOHandler;
import bitzero.engine.io.IRequest;
import bitzero.engine.io.IResponse;
import bitzero.engine.io.Request;
import bitzero.engine.io.protocols.AbstractProtocolCodec;
import bitzero.engine.sessions.ISession;
import java.util.Collection;

public class TextProtocolCodec
extends AbstractProtocolCodec {
    private String defaultControllerId = "text";

    public String getDefaultControllerId() {
        return this.defaultControllerId;
    }

    public void setDefaultControllerId(String defaultControllerId) {
        this.defaultControllerId = defaultControllerId;
    }

    @Override
    public void onPacketRead(IPacket packet) {
        this.dispatchRequestToController(this.packet2Request(packet), this.defaultControllerId);
    }

    @Override
    public void onPacketWrite(IResponse response) {
        Packet packet = new Packet();
        packet.setId((Short)response.getId());
        packet.setRecipients(response.getRecipients());
        packet.setData(response.getContent());
        this.ioHandler.onDataWrite(packet);
    }

    private IRequest packet2Request(IPacket packet) {
        Request request = new Request();
        String message = (String)packet.getData();
        String[] slices = message.split("\\:");
        if (slices.length == 2) {
            request.setId(slices[0]);
            request.setContent(slices[1]);
        } else {
            request.setId("generic");
            request.setContent(message);
        }
        request.setSender(packet.getSender());
        return request;
    }
}

