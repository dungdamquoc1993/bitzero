/*
 * Decompiled with CFR 0_116.
 */
package bitzero.engine.data;

import bitzero.engine.data.MessagePriority;
import bitzero.engine.data.TransportType;
import bitzero.engine.sessions.ISession;
import java.util.Collection;

public interface IPacket {
    public Object getData();

    public void setData(Object var1);

    public TransportType getTransportType();

    public void setTransportType(TransportType var1);

    public MessagePriority getPriority();

    public void setPriority(MessagePriority var1);

    public Collection getRecipients();

    public void setRecipients(Collection var1);

    public ISession getSender();

    public void setSender(ISession var1);

    public Object getAttribute(String var1);

    public void setAttribute(String var1, Object var2);

    public String getOwnerNode();

    public void setOwnerNode(String var1);

    public long getCreationTime();

    public void setCreationTime(long var1);

    public int getOriginalSize();

    public void setOriginalSize(int var1);

    public boolean isTcp();

    public boolean isUdp();

    public IPacket clone();

    public byte[] getFragmentBuffer();

    public void setFragmentBuffer(byte[] var1);

    public boolean isFragmented();

    public void setId(Short var1);

    public Short getId();
}

