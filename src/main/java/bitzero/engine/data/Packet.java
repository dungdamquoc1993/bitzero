/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.data;

import bitzero.engine.data.IPacket;
import bitzero.engine.data.MessagePriority;
import bitzero.engine.data.TransportType;
import bitzero.engine.sessions.ISession;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Packet
implements IPacket {
    protected Short id = 0;
    protected long creationTime = System.nanoTime();
    protected Object data;
    protected String ownerNode;
    protected MessagePriority priority = MessagePriority.NORMAL;
    protected ISession sender;
    protected TransportType transportType = TransportType.TCP;
    protected int originalSize = -1;
    protected ConcurrentMap attributes;
    protected Collection recipients;
    protected byte[] fragmentBuffer;

    @Override
    public Object getAttribute(String key) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object attr) {
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap();
        }
        this.attributes.put(key, attr);
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean isFragmented() {
        return this.fragmentBuffer != null;
    }

    @Override
    public byte[] getFragmentBuffer() {
        return this.fragmentBuffer;
    }

    @Override
    public void setFragmentBuffer(byte[] bb) {
        this.fragmentBuffer = bb;
    }

    private byte[] cloneData(Object data) {
        if (data instanceof byte[]) {
            byte[] newData = new byte[((byte[])data).length];
            System.arraycopy(data, 0, newData, 0, newData.length);
            return newData;
        }
        return null;
    }

    @Override
    public String getOwnerNode() {
        return this.ownerNode;
    }

    @Override
    public void setOwnerNode(String ownerNode) {
        this.ownerNode = ownerNode;
    }

    @Override
    public MessagePriority getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(MessagePriority priority) {
        this.priority = priority;
    }

    @Override
    public ISession getSender() {
        return this.sender;
    }

    @Override
    public void setSender(ISession sender) {
        this.sender = sender;
    }

    @Override
    public TransportType getTransportType() {
        return this.transportType;
    }

    @Override
    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    @Override
    public Collection getRecipients() {
        return this.recipients;
    }

    @Override
    public void setRecipients(Collection recipients) {
        this.recipients = recipients;
    }

    @Override
    public boolean isTcp() {
        return this.transportType == TransportType.TCP;
    }

    @Override
    public boolean isUdp() {
        return this.transportType == TransportType.UDP;
    }

    @Override
    public int getOriginalSize() {
        return this.originalSize;
    }

    @Override
    public void setOriginalSize(int originalSize) {
        if (this.originalSize == -1) {
            this.originalSize = originalSize;
        }
    }

    public String toString() {
        return String.format("{\u00a0Packet: %s, data: %s, Pri: %s }", new Object[]{this.transportType, this.data.getClass().getName(), this.priority});
    }

    @Override
    public IPacket clone() {
        Packet newPacket = new Packet();
        newPacket.setCreationTime(this.getCreationTime());
        newPacket.setId(this.getId());
        newPacket.setData(this.getData());
        newPacket.setOriginalSize(this.getOriginalSize());
        newPacket.setOwnerNode(this.getOwnerNode());
        newPacket.setPriority(this.getPriority());
        newPacket.setRecipients(null);
        newPacket.setSender(this.getSender());
        newPacket.setTransportType(this.getTransportType());
        return newPacket;
    }

    @Override
    public Short getId() {
        return this.id;
    }

    @Override
    public void setId(Short _id) {
        this.id = _id;
    }
}

