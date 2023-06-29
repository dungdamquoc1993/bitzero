/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.core;

import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.config.EngineConstants;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineWriter;
import bitzero.engine.data.BufferType;
import bitzero.engine.data.IPacket;
import bitzero.engine.events.Event;
import bitzero.engine.events.IEvent;
import bitzero.engine.exceptions.MessageQueueFullException;
import bitzero.engine.exceptions.PacketQueueWarning;
import bitzero.engine.io.IOHandler;
import bitzero.engine.service.BaseCoreService;
import bitzero.engine.sessions.IPacketQueue;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.sessions.SessionType;
import bitzero.engine.util.Logging;
import bitzero.engine.util.NetworkServices;
import bitzero.server.BitZeroServer;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.IUserManager;
import bitzero.server.util.PacketCount;
import bitzero.server.util.PacketType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EngineWriter
extends BaseCoreService
implements IEngineWriter,
Runnable {
    private BitZeroEngine engine;
    private IOHandler ioHandler;
    private final Logger logger;
    private final Logger bootLogger;
    private final ExecutorService threadPool;
    private final BlockingQueue sessionTicketsQueue;
    private volatile int threadId;
    private volatile boolean isActive;
    private volatile long droppedPacketsCount;
    private volatile long writtenBytes;
    private volatile long writtenPackets;
    private ISessionManager sessionManager;
    private int threadPoolSize;
    private boolean isClustered;

    public EngineWriter(int threadPoolSize) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.bootLogger = LoggerFactory.getLogger((String)"bootLogger");
        this.sessionTicketsQueue = new LinkedBlockingQueue();
        this.threadId = 1;
        this.isActive = false;
        this.droppedPacketsCount = 0;
        this.writtenBytes = 0;
        this.writtenPackets = 0;
        this.threadPoolSize = threadPoolSize;
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    @Override
    public void init(Object o) {
        super.init(o);
        if (this.isActive) {
            throw new IllegalArgumentException("Object is already initialized. Destroy it first!");
        }
        if (this.threadPoolSize < 1) {
            throw new IllegalArgumentException("Illegal value for a thread pool size: " + this.threadPoolSize);
        }
        this.engine = BitZeroEngine.getInstance();
        this.sessionManager = this.engine.getSessionManager();
        this.isActive = true;
        this.isClustered = this.engine.getConfiguration().isClustered();
        this.initThreadPool();
        this.bootLogger.info("Socket Writer started (pool size:" + this.threadPoolSize + ")");
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
        this.isActive = false;
        List<Runnable> leftOvers = this.threadPool.shutdownNow();
        this.bootLogger.info("EngineWriter stopped. Unprocessed tasks: " + leftOvers.size());
    }

    @Override
    public int getQueueSize() {
        return this.sessionTicketsQueue.size();
    }

    @Override
    public int getThreadPoolSize() {
        return this.threadPoolSize;
    }

    @Override
    public IOHandler getIOHandler() {
        return this.ioHandler;
    }

    @Override
    public void setIOHandler(IOHandler ioHandler) {
        if (this.ioHandler != null) {
            throw new IllegalStateException("You cannot reassign the IOHandler class!");
        }
        this.ioHandler = ioHandler;
    }

    @Override
    public void continueWriteOp(ISession session) {
        if (session != null) {
            this.sessionTicketsQueue.add(session);
        }
    }

    private void initThreadPool() {
        for (int j = 0; j < this.threadPoolSize; ++j) {
            this.threadPool.execute(this);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("EngineWriter-" + this.threadId++);
        ByteBuffer writeBuffer = NetworkServices.allocateBuffer(32768, this.engine.getConfiguration().getWriteBufferType());
        while (this.isActive) {
            try {
                ISession session = (ISession)this.sessionTicketsQueue.take();
                this.processSessionQueue(writeBuffer, session);
            }
            catch (InterruptedException e) {
                this.logger.warn("EngineWriter thread interruped: " + Thread.currentThread());
                this.isActive = false;
            }
            catch (Throwable t) {
                this.logger.warn("Problems in EngineWriter main loop, Thread: " + Thread.currentThread());
                Logging.logStackTrace(this.logger, t);
            }
        }
        this.bootLogger.info("EngineWriter threadpool shutting down.");
    }

    private void processSessionQueue(ByteBuffer writeBuffer, ISession session) {
        if (session != null) {
            SessionType type = session.getType();
            if (type == SessionType.DEFAULT) {
                this.processRegularSession(writeBuffer, session);
            } else if (type == SessionType.BLUEBOX) {
                this.processBlueBoxSession(session);
            } else if (type == SessionType.VOID) {
                return;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processBlueBoxSession(ISession session) {
        IPacketQueue sessionQ = session.getPacketQueue();
        IPacket packet = null;
        IPacketQueue iPacketQueue = sessionQ;
        synchronized (iPacketQueue) {
            if (!sessionQ.isEmpty()) {
                packet = sessionQ.take();
            }
        }
        if (packet != null) {
            // empty if block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processRegularSession(ByteBuffer writeBuffer, ISession session) {
        IPacket packet = null;
        try {
            IPacketQueue sessionQ;
            IPacketQueue iPacketQueue = sessionQ = session.getPacketQueue();
            synchronized (iPacketQueue) {
                if (sessionQ.isEmpty()) {
                    return;
                }
                if (session.isFrozen()) {
                    sessionQ.take();
                    return;
                }
                packet = sessionQ.peek();
                if (packet == null) {
                    if (!sessionQ.isEmpty()) {
                        sessionQ.take();
                    }
                    return;
                }
                if (packet.isTcp()) {
                    this.tcpSend(writeBuffer, sessionQ, session, packet);
                } else if (packet.isUdp()) {
                    this.udpSend(writeBuffer, sessionQ, session, packet);
                } else {
                    this.logger.warn("Unknow packet type: " + packet);
                }
            }
            return;
        }
        catch (ClosedChannelException cce) {
            this.logger.warn("Socket closed during write operation for session: " + session);
        }
        catch (IOException ioexception) {
            this.logger.warn("Error during write. Session: " + session);
        }
        catch (Exception e) {
            this.logger.warn("Error during write. Session: " + session);
            Logging.logStackTrace(this.logger, e);
        }
    }

    private void tcpSend(ByteBuffer writeBuffer, IPacketQueue sessionQ, ISession session, IPacket packet) throws Exception {
        byte[] buffer;
        SocketChannel channel = session.getConnection();
        if (channel == null) {
            this.logger.debug("Skipping packet, found null socket for Session: " + session);
            return;
        }
        writeBuffer.clear();
        byte[] arrby = buffer = packet.isFragmented() ? packet.getFragmentBuffer() : (byte[])packet.getData();
        if (writeBuffer.capacity() < buffer.length) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Allocating new buffer. Curr. capacity: " + writeBuffer.capacity() + ", Need: " + buffer.length);
            }
            writeBuffer = NetworkServices.allocateBuffer(buffer.length, this.engine.getConfiguration().getWriteBufferType());
        }
        writeBuffer.put(buffer);
        writeBuffer.flip();
        long toWrite = writeBuffer.remaining();
        long bytesWritten = channel.write(writeBuffer);
        this.writtenBytes += bytesWritten;
        session.addWrittenBytes(bytesWritten);
        if (bytesWritten < toWrite) {
            byte[] bb = new byte[writeBuffer.remaining()];
            writeBuffer.get(bb);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("<<< Partial Socket Write >>>");
                this.logger.trace("Remaining: " + bb.length);
            }
            packet.setFragmentBuffer(bb);
            SelectionKey sk = (SelectionKey)session.getSystemProperty("SessionSelectionKey");
            if (sk != null && sk.isValid()) {
                sk.interestOps(5);
            } else {
                this.logger.warn("Could not OP_WRITE for Session: " + session);
            }
        } else {
            BitZeroServer.getInstance().getPacketCount().addPacket(PacketType.OUTGOING, packet.getId());
            ++this.writtenPackets;
            sessionQ.take();
            if (!sessionQ.isEmpty()) {
                this.sessionTicketsQueue.add(session);
            }
        }
    }

    private void udpSend(ByteBuffer writeBuffer, IPacketQueue sessionQ, ISession session, IPacket packet) throws Exception {
        sessionQ.take();
        if (!sessionQ.isEmpty()) {
            this.sessionTicketsQueue.add(session);
        }
        writeBuffer.clear();
        byte[] buffer = (byte[])packet.getData();
        if (writeBuffer.capacity() < buffer.length) {
            this.logger.trace("Allocating new buffer. Curr. capacity: " + writeBuffer.capacity() + ", Need: " + buffer.length);
            writeBuffer = NetworkServices.allocateBuffer(buffer.length, EngineConstants.DEFAULT_WRITE_BUFFER_TYPE);
        }
        writeBuffer.put(buffer);
        writeBuffer.flip();
        DatagramChannel datagramChannel = session.getDatagramChannel();
        Integer sessionUdpPort = (Integer)session.getSystemProperty("UDPPort");
        if (datagramChannel == null) {
            throw new IllegalStateException("UDP Packet cannot be sent to: " + session + ", no DatagramChannel was ever set!");
        }
        if (sessionUdpPort == null) {
            throw new IllegalStateException("UDP Packet cannot be sent to: " + session + ", no UDP port set.");
        }
        int written = datagramChannel.send(writeBuffer, new InetSocketAddress(session.getAddress(), (int)sessionUdpPort));
        if (written != 0) {
            this.writtenBytes += (long)written;
            session.addWrittenBytes(this.writtenBytes);
        }
    }

    @Override
    public void enqueuePacket(IPacket packet) {
        if (this.isClustered) {
            this.enqueueClustered(packet);
        } else {
            this.enqueueLocal(packet);
        }
    }

    private void enqueueClustered(IPacket packet) {
    }

    private void enqueueLocal(IPacket packet) {
        Collection<ISession> recipients = packet.getRecipients();
        int size = 0;
        if (recipients != null) {
            size = recipients.size();
        }
        if (size > 0) {
            if (packet.getSender() != null) {
                packet.getSender().setLastWriteTime(System.currentTimeMillis());
            }
            if (size == 1) {
                this.enqueueLocalPacket((ISession)packet.getRecipients().iterator().next(), packet);
            } else {
                for (ISession session : recipients) {
                    this.enqueueLocalPacket(session, packet.clone());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void enqueueLocalPacket(ISession session, IPacket packet) {
        boolean isBlueBoxed;
        if (session.isFrozen()) {
            return;
        }
        IPacketQueue sessionQ = session.getPacketQueue();
        boolean bl = isBlueBoxed = session.getType() == SessionType.BLUEBOX;
        if (sessionQ != null) {
            IPacketQueue iPacketQueue = sessionQ;
            synchronized (iPacketQueue) {
                int userId = 0;
                try {
                    boolean wasEmpty = sessionQ.isEmpty();
                    sessionQ.put(packet);
                    if (wasEmpty || !this.sessionTicketsQueue.contains(session) || isBlueBoxed) {
                        this.sessionTicketsQueue.add(session);
                    }
                    packet.setRecipients(null);
                }
                catch (PacketQueueWarning err) {
                    User user = BitZeroServer.getInstance().getUserManager().getUserBySession(session);
                    if (user != null) {
                        userId = user.getId();
                    }
                    this.dropOneMessage(packet.getId(), session, userId, false);
                    this.logger.warn(String.valueOf(err.getMessage()) + ": " + session);
                }
                catch (MessageQueueFullException error) {
                    this.dropOneMessage(packet.getId(), session, userId, true);
                }
            }
        }
    }

    private void dropOneMessage(Short id, ISession session, int userId, boolean isFull) {
        session.addDroppedMessages(1);
        ++this.droppedPacketsCount;
        if (isFull) {
            BitZeroServer.getInstance().getPacketCount().addPacket(PacketType.OUT_DROP_FULL, id);
        } else {
            BitZeroServer.getInstance().getPacketCount().addPacket(PacketType.OUT_DROP_WARN, id, userId);
        }
        Event event = new Event("packetDropped");
        event.setParameter("session", session);
        this.dispatchEvent(event);
    }

    @Override
    public long getDroppedPacketsCount() {
        return this.droppedPacketsCount;
    }

    @Override
    public long getWrittenBytes() {
        return this.writtenBytes;
    }

    @Override
    public long getWrittenPackets() {
        return this.writtenPackets;
    }
}

