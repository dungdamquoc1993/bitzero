/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.core;

import bitzero.engine.config.EngineConfiguration;
import bitzero.engine.config.SocketConfig;
import bitzero.engine.core.BitZeroEngine;
import bitzero.engine.core.IEngineAcceptor;
import bitzero.engine.core.IEngineReader;
import bitzero.engine.core.security.DefaultConnectionFilter;
import bitzero.engine.core.security.IConnectionFilter;
import bitzero.engine.data.BindableSocket;
import bitzero.engine.data.TransportType;
import bitzero.engine.events.Event;
import bitzero.engine.events.IEvent;
import bitzero.engine.exceptions.RefusedAddressException;
import bitzero.engine.service.BaseCoreService;
import bitzero.engine.sessions.ISession;
import bitzero.engine.sessions.ISessionManager;
import bitzero.engine.util.Logging;
import bitzero.util.datacontroller.business.DataController;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineAcceptor
extends BaseCoreService
implements IEngineAcceptor,
Runnable {
    private final BitZeroEngine engine;
    private final Logger logger;
    private final Logger bootLogger;
    private volatile int threadId = 1;
    private int threadPoolSize = 1;
    private final ExecutorService threadPool;
    private List acceptableConnections;
    private List<BindableSocket> boundSockets;
    private IConnectionFilter connectionFilter;
    private ISessionManager sessionManager;
    private IEngineReader socketReader;
    private Selector acceptSelector;
    private volatile boolean isActive = false;

    public EngineAcceptor() {
        this(1);
    }

    public EngineAcceptor(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        this.engine = BitZeroEngine.getInstance();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.bootLogger = LoggerFactory.getLogger((String)"bootLogger");
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
        this.acceptableConnections = new ArrayList();
        this.boundSockets = new ArrayList<>();
        this.socketReader = this.engine.getEngineReader();
        this.connectionFilter = new DefaultConnectionFilter();
        try {
            this.acceptSelector = Selector.open();
            this.bootLogger.info("AcceptSelector opened");
        }
        catch (IOException e) {
            this.bootLogger.warn("Problems during EngineAcceptor init: " + e);
            Logging.logStackTrace(this.bootLogger, e);
        }
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
        this.sessionManager = this.engine.getSessionManager();
        this.isActive = true;
        this.initThreadPool();
        this.bootLogger.info("EngineAcceptor initialized");
        this.checkBoundSockets();
    }

    @Override
    public void destroy(Object o) {
        super.destroy(o);
        this.isActive = false;
        this.shutDownBoundSockets();
        List<Runnable> leftOvers = this.threadPool.shutdownNow();
        try {
            Thread.sleep(500);
            this.acceptSelector.close();
        }
        catch (Exception e) {
            this.bootLogger.warn("Error when shutting down Accept selector: " + e.getMessage());
        }
        this.bootLogger.info("EngineAcceptor stopped. Unprocessed tasks: " + leftOvers.size());
    }

    private void initThreadPool() {
        for (int j = 0; j < this.threadPoolSize; ++j) {
            this.threadPool.execute(this);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("EngineAcceptor-" + this.threadId++);
        while (this.isActive) {
            try {
                this.acceptLoop();
            }
            catch (IOException e) {
                this.logger.info("I/O Error with Accept Selector: " + e.getMessage());
                Logging.logStackTrace(this.logger, e);
            }
        }
        this.bootLogger.info("EngineAcceptor threadpool shutting down.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void acceptLoop() throws IOException {
        this.acceptSelector.select();
        Set<SelectionKey> readyKeys = this.acceptSelector.selectedKeys();
        SelectionKey key = null;
        Iterator<SelectionKey> it = readyKeys.iterator();
        while (it.hasNext()) {
            try {
                key = it.next();
                it.remove();
                ServerSocketChannel ssChannel = (ServerSocketChannel)key.channel();
                SocketChannel clientChannel = ssChannel.accept();
                this.logger.trace("Accepted client connection on: " + ssChannel.socket().getInetAddress().getHostAddress() + ":" + ssChannel.socket().getLocalPort());
                List list = this.acceptableConnections;
                synchronized (list) {
                    this.acceptableConnections.add(clientChannel);
                    continue;
                }
            }
            catch (IOException error) {
                this.logger.info("I/O Error during accept loop: " + error.getMessage());
                continue;
            }
        }
        if (this.isActive) {
            this.socketReader.getSelector().wakeup();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleAcceptableConnections() {
        if (this.acceptableConnections.size() == 0) {
            return;
        }
        List list = this.acceptableConnections;
        synchronized (list) {
            Iterator it = this.acceptableConnections.iterator();
            while (it.hasNext()) {
                SocketChannel connection = (SocketChannel)it.next();
                it.remove();
                InetAddress iAddr = connection.socket().getInetAddress();
                if (iAddr == null) continue;
                try {
                    this.connectionFilter.validateAndAddAddress(iAddr.getHostAddress());
                    connection.configureBlocking(false);
                    connection.socket().setTcpNoDelay(!this.engine.getConfiguration().isNagleAlgorithm());
                    SelectionKey selectionKey = connection.register(this.socketReader.getSelector(), 1);
                    ISession session = this.sessionManager.createSession(connection);
                    session.setSystemProperty("SessionSelectionKey", selectionKey);
                    this.sessionManager.addSession(session);
                    this.logger.info("Session created: " + session + " on Server port: " + connection.socket().getLocalPort() + " <---> " + session.getClientPort());
                    Event sessionAddedEvent = new Event("sessionAdded");
                    sessionAddedEvent.setParameter("session", session);
                    this.dispatchEvent(sessionAddedEvent);
                }
                catch (RefusedAddressException e) {
                    this.logger.info("Refused connection. " + e.getMessage());
                    try {
                        connection.socket().shutdownInput();
                        connection.socket().shutdownOutput();
                        connection.close();
                        continue;
                    }
                    catch (IOException e1) {
                        this.logger.warn("Additional problem with refused connection. Was not able to shut down the channel: " + e1.getMessage());
                    }
                }
                catch (IOException e) {
                    StringBuilder sb = new StringBuilder("Failed accepting connection: ");
                    if (connection != null && connection.socket() != null) {
                        sb.append(connection.socket().getInetAddress().getHostAddress());
                    }
                    this.logger.info(sb.toString());
                }
            }
        }
    }

    @Override
    public void bindSocket(SocketConfig socketConfig) throws IOException {
        if (socketConfig.getType() == TransportType.TCP) {
            this.bindTcpSocket(socketConfig.getAddress(), socketConfig.getPort());
        } else if (socketConfig.getType() == TransportType.UDP) {
            this.bindUdpSocket(socketConfig.getAddress(), socketConfig.getPort());
        } else {
            throw new UnsupportedOperationException("Invalid transport type!");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List getBoundSockets() {
        ArrayList list = null;
        List list2 = this.boundSockets;
        synchronized (list2) {
            list = new ArrayList(this.boundSockets);
        }
        return list;
    }

    @Override
    public IConnectionFilter getConnectionFilter() {
        return this.connectionFilter;
    }

    @Override
    public void setConnectionFilter(IConnectionFilter filter) {
        if (this.connectionFilter != null) {
            throw new IllegalStateException("A connection filter already exists!");
        }
        this.connectionFilter = filter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void bindTcpSocket(String address, int port) throws IOException {
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.socket().bind(new InetSocketAddress(address, port));
        socketChannel.socket().setReuseAddress(true);
        socketChannel.register(this.acceptSelector, 16);
        List list = this.boundSockets;
        synchronized (list) {
            this.boundSockets.add(new BindableSocket(socketChannel, address, port, TransportType.TCP));
        }
        this.bootLogger.info("Added bound tcp socket --> " + address + ":" + port);
        this.setCacheBindPort(address, port);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void bindUdpSocket(String address, int port) throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.socket().bind(new InetSocketAddress(address, port));
        datagramChannel.socket().setReuseAddress(true);
        datagramChannel.register(this.socketReader.getSelector(), 1);
        List list = this.boundSockets;
        synchronized (list) {
            this.boundSockets.add(new BindableSocket(datagramChannel, address, port, TransportType.UDP));
        }
        this.bootLogger.info("Added bound udp socket --> " + address + ":" + port);
        this.setCacheBindPort(address, port);
    }

    private void setCacheBindPort(String address, int port) {
        try {
            DataController.getController().set("cache_binded_port__" + address + "_" + port, true);
        }
        catch (Exception var3_3) {
            // empty catch block
        }
    }

    private void bindBlueBox() throws IOException {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    private void checkBoundSockets() {
        if (this.boundSockets.size() < 1) {
            this.bootLogger.error("No bound sockets! Check the boot logs for possible problems!");
        }
    }

    private void shutDownBoundSockets() {
        ArrayList<BindableSocket> problematicSockets = null;
        for (BindableSocket bindableSocket : this.boundSockets) {
            try {
                bindableSocket.getChannel().close();
            }
            catch (IOException e) {
                if (problematicSockets == null) {
                    problematicSockets = new ArrayList<BindableSocket>();
                }
                problematicSockets.add(bindableSocket);
            }
        }
        if (problematicSockets != null) {
            StringBuilder sb = new StringBuilder("Problems closing bound socket(s). The following socket(s) raised exceptions: ");
            for (BindableSocket socket : problematicSockets) {
                sb.append(socket.toString()).append(" ");
            }
            throw new RuntimeException(sb.toString());
        }
    }
}

