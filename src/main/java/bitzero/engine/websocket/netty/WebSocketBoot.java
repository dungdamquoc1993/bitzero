/*
 * Decompiled with CFR 0_116.
 * 
 * Could not load the following classes:
 *  org.jboss.netty.bootstrap.ServerBootstrap
 *  org.jboss.netty.channel.Channel
 *  org.jboss.netty.channel.ChannelFactory
 *  org.jboss.netty.channel.ChannelPipelineFactory
 *  org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package bitzero.engine.websocket.netty;

import bitzero.engine.io.IProtocolCodec;
import bitzero.engine.util.Logging;
import bitzero.engine.websocket.WebSocketConfig;
import bitzero.engine.websocket.netty.WebSocketServerPipelineFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketBoot {
    private final WebSocketConfig config;
    private final IProtocolCodec protocolCodec;
    private final Logger logger = LoggerFactory.getLogger((String)"debug");

    public WebSocketBoot(WebSocketConfig cfg, IProtocolCodec codec) {
        this.config = cfg;
        this.protocolCodec = codec;
        if (!cfg.isActive()) {
            return;
        }
        try {
            this.boot();
            this.logger.info("WebSocketService started: " + this.config.getHost() + ":" + this.config.getPort());
            if (this.config.isSSL()) {
                this.bootSSL();
                this.logger.info("WebSocketService SSL started: " + this.config.getHost() + ":" + this.config.getSslPort());
            }
        }
        catch (Exception problem) {
            this.logger.error("Failed starting up websocket engine: " + problem.getMessage());
            Logging.logStackTrace(this.logger, problem);
        }
    }

    private void boot() throws Exception {
        ServerBootstrap boot;
        int webSocketPort = this.config.getPort();
        if (!this.config.isUsingFixThreadPool()) {
            this.logger.info("Websocket Using Cached Thread Pool");
            boot = new ServerBootstrap((ChannelFactory)new NioServerSocketChannelFactory((Executor)Executors.newCachedThreadPool(), (Executor)Executors.newCachedThreadPool()));
        } else {
            this.logger.info("Websocket Using Fixed Thread Pool");
            boot = new ServerBootstrap((ChannelFactory)new NioServerSocketChannelFactory((Executor)Executors.newFixedThreadPool(this.config.getBossThreadNum()), (Executor)Executors.newFixedThreadPool(this.config.getWorkerThreadNum())));
        }
        boot.setPipelineFactory((ChannelPipelineFactory)new WebSocketServerPipelineFactory(this.protocolCodec, false, this.config.isAutoBahnTest()));
        boot.bind((SocketAddress)new InetSocketAddress(this.config.getHost(), webSocketPort));
    }

    public void bootSSL() {
        ServerBootstrap boot;
        System.setProperty("keystore.file.path", this.config.getKeyStoreFile());
        System.setProperty("keystore.file.password", this.config.getKeyStorePassword());
        int webSocketPort = this.config.getSslPort();
        if (!this.config.isUsingFixThreadPool()) {
            this.logger.info("Websocket Using Cached Thread Pool");
            boot = new ServerBootstrap((ChannelFactory)new NioServerSocketChannelFactory((Executor)Executors.newCachedThreadPool(), (Executor)Executors.newCachedThreadPool()));
        } else {
            this.logger.info("Websocket Using Fixed Thread Pool");
            boot = new ServerBootstrap((ChannelFactory)new NioServerSocketChannelFactory((Executor)Executors.newFixedThreadPool(this.config.getBossThreadNum()), (Executor)Executors.newFixedThreadPool(this.config.getWorkerThreadNum())));
        }
        boot.setPipelineFactory((ChannelPipelineFactory)new WebSocketServerPipelineFactory(this.protocolCodec, true, this.config.isAutoBahnTest()));
        boot.bind((SocketAddress)new InetSocketAddress(this.config.getHost(), webSocketPort));
    }
}

