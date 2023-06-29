/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.core;

import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineAcceptor;
import bitzero.engine.core.IEngineReader;
import bitzero.engine.core.IEngineWriter;
import bitzero.engine.data.BufferType;
import bitzero.engine.io.IOHandler;
import bitzero.engine.service.BaseCoreService;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.util.Logging;
import bitzero.engine.util.NetworkServices;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineReader
extends BaseCoreService
implements IEngineReader,
Runnable {
    private final BitZeroEngine engine;
    private final Logger logger;
    private final Logger bootLogger;
    private int threadPoolSize;
    private final ExecutorService threadPool;
    private ISessionManager sessionManager;
    private IEngineAcceptor socketAcceptor;
    private IEngineWriter socketWriter;
    private Selector readSelector;
    private IOHandler ioHandler;
    private volatile boolean isActive = false;
    private volatile long readBytes = 0;
    private volatile int threadId = 0;

    public EngineReader() {
        this(1);
    }

    public EngineReader(int nThreads) {
        this.threadPoolSize = nThreads;
        this.threadPool = Executors.newSingleThreadExecutor();
        this.engine = BitZeroEngine.getInstance();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.bootLogger = LoggerFactory.getLogger((String)"bootLogger");
        try {
            this.readSelector = Selector.open();
            this.bootLogger.info("TCP Selector opened");
        }
        catch (IOException e) {
            this.bootLogger.error("Failed opening Read Selector: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void init(Object o) {
        super.init(o);
        if (this.isActive) {
            throw new IllegalArgumentException("Object is already initialized. Destroy it first!");
        }
        this.sessionManager = this.engine.getSessionManager();
        this.socketAcceptor = this.engine.getEngineAcceptor();
        this.socketWriter = this.engine.getEngineWriter();
        this.isActive = true;
        this.initThreadPool();
        this.bootLogger.info("IOHandler: " + this.ioHandler);
        this.bootLogger.info("EngineReader started");
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
        this.isActive = false;
        List<Runnable> leftOvers = this.threadPool.shutdownNow();
        try {
            Thread.sleep(500);
            this.readSelector.close();
        }
        catch (Exception e) {
            this.bootLogger.warn("Error when shutting down Accept TCP selector: " + e.getMessage());
            Logging.logStackTrace(this.bootLogger, e);
        }
        this.bootLogger.info("EngineReader stopped. Unprocessed tasks: " + leftOvers.size());
    }

    public void initThreadPool() {
        for (int j = 0; j < this.threadPoolSize; ++j) {
            this.threadPool.execute(this);
        }
    }

    @Override
    public void run() {
        ByteBuffer readBuffer = NetworkServices.allocateBuffer(this.engine.getConfiguration().getReadMaxBufferSize(), this.engine.getConfiguration().getReadBufferType());
        Thread.currentThread().setName("EngineReader-" + this.threadId++);
        while (this.isActive) {
            try {
                this.socketAcceptor.handleAcceptableConnections();
                this.readIncomingSocketData(readBuffer);
                Thread.sleep(5);
            }
            catch (Throwable t) {
                this.logger.warn("Problems in EngineReader main loop: " + t + ", Thread: " + Thread.currentThread());
                Logging.logStackTrace(this.logger, t);
            }
        }
        this.bootLogger.info("EngineReader threadpool shutting down.");
    }

    private void readIncomingSocketData(ByteBuffer readBuffer) {
        SocketChannel channel = null;
        SelectionKey key = null;
        try {
            int readyKeyCount = this.readSelector.selectNow();
            if (readyKeyCount > 0) {
                Set<SelectionKey> readyKeys = this.readSelector.selectedKeys();
                Iterator<SelectionKey> it = readyKeys.iterator();
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    if (!key.isValid()) continue;
                    channel = (SocketChannel)key.channel();
                    readBuffer.clear();
                    try {
                        this.readTcpData(channel, key, readBuffer);
                    }
                    catch (IOException e) {
                        this.closeTcpConnection(channel);
                        this.logger.info("Socket closed: " + channel);
                    }
                }
            }
        }
        catch (ClosedSelectorException e) {
            this.logger.debug("Selector is closed!");
        }
        catch (CancelledKeyException cancelledkeyexception) {
            this.logger.debug("Cancelled Key!");
        }
        catch (IOException ioe) {
            this.logger.warn("I/O reading/selection error: " + ioe);
            Logging.logStackTrace(this.logger, ioe);
        }
        catch (Exception err) {
            this.logger.warn("Generic reading/selection error: " + err);
            Logging.logStackTrace(this.logger, err);
        }
    }

    private void readTcpData(SocketChannel channel, SelectionKey key, ByteBuffer readBuffer) throws IOException {
        ISession session = this.sessionManager.getLocalSessionByConnection(channel);
        if (key.isWritable()) {
            key.interestOps(1);
            this.socketWriter.continueWriteOp(session);
        }
        if (!key.isReadable()) {
            return;
        }
        readBuffer.clear();
        long byteCount = 0;
        byteCount = channel.read(readBuffer);
        if (byteCount == -1) {
            this.closeTcpConnection(channel);
        } else if (byteCount > 0) {
            session.setLastReadTime(System.currentTimeMillis());
            this.readBytes += byteCount;
            session.addReadBytes(byteCount);
            readBuffer.flip();
            byte[] binaryData = new byte[readBuffer.limit()];
            readBuffer.get(binaryData);
            this.ioHandler.onDataRead(session, binaryData);
        }
    }

    private void closeTcpConnection(SelectableChannel channel) throws IOException {
        channel.close();
        if (channel instanceof SocketChannel) {
            this.sessionManager.onSocketDisconnected((SocketChannel)channel);
        }
    }

    private void readUdpData(DatagramChannel channel) throws IOException {
    }

    @Override
    public IOHandler getIOHandler() {
        return this.ioHandler;
    }

    @Override
    public Selector getSelector() {
        return this.readSelector;
    }

    @Override
    public void setIoHandler(IOHandler handler) {
        if (handler == null) {
            throw new IllegalStateException("IOHandler was already injected!");
        }
        this.ioHandler = handler;
    }

    @Override
    public long getReadBytes() {
        return this.readBytes;
    }

    @Override
    public long getReadPackets() {
        return this.ioHandler.getReadPackets();
    }
}

