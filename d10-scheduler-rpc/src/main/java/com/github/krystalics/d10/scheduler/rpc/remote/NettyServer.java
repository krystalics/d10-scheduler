/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.krystalics.d10.scheduler.rpc.remote;


import com.github.krystalics.d10.scheduler.rpc.codec.NettyDecoder;
import com.github.krystalics.d10.scheduler.rpc.codec.NettyEncoder;
import com.github.krystalics.d10.scheduler.rpc.common.RpcRequest;
import com.github.krystalics.d10.scheduler.rpc.config.NettyServerConfig;
import com.github.krystalics.d10.scheduler.rpc.utils.Constants;
import com.github.krystalics.d10.scheduler.rpc.utils.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import net.openhft.affinity.AffinityStrategies;
import net.openhft.affinity.AffinityThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NettyServer
 */
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    /**
     * boss group
     */
    private final EventLoopGroup bossGroup;

    /**
     * worker group
     */
    private final EventLoopGroup workGroup;

    /**
     * server config
     */
    private final NettyServerConfig serverConfig;

    /**
     * server bootstrap
     */
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    /**
     * started flag
     */
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * server init
     *
     * @param serverConfig server config
     */
    public NettyServer(final NettyServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        if (NettyUtils.useEpoll()) {
            this.bossGroup = new EpollEventLoopGroup(1, new AffinityThreadFactory("NettyServerBossThread", AffinityStrategies.DIFFERENT_CORE));
            this.workGroup = new EpollEventLoopGroup(serverConfig.getWorkerThread(), new AffinityThreadFactory("NettyServerWorkerThread", AffinityStrategies.DIFFERENT_CORE));
        } else {
            this.bossGroup = new NioEventLoopGroup(1, new AffinityThreadFactory("NettyServerBossThread", AffinityStrategies.DIFFERENT_CORE));
            this.workGroup = new NioEventLoopGroup(serverConfig.getWorkerThread(), new AffinityThreadFactory("NettyServerWorkerThread", AffinityStrategies.DIFFERENT_CORE));
        }
        this.start();
    }

    /**
     * server start
     */
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            this.serverBootstrap
                    .group(this.bossGroup, this.workGroup)
                    .channel(NettyUtils.getServerSocketChannelClass())
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_BACKLOG, serverConfig.getSoBacklog())
                    .childOption(ChannelOption.SO_KEEPALIVE, serverConfig.isSoKeepalive())
                    .childOption(ChannelOption.TCP_NODELAY, serverConfig.isTcpNoDelay())
                    .childOption(ChannelOption.SO_SNDBUF, serverConfig.getSendBufferSize())
                    .childOption(ChannelOption.SO_RCVBUF, serverConfig.getReceiveBufferSize())
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            initNettyChannel(ch);
                        }
                    });

            ChannelFuture future;
            try {
                future = serverBootstrap.bind(serverConfig.getListenPort()).sync();
            } catch (Exception e) {
                logger.error("NettyRemotingServer bind fail {}, exit", e.getMessage(), e);
                throw new RuntimeException(String.format("NettyRemotingServer bind %s fail", serverConfig.getListenPort()));
            }
            if (future.isSuccess()) {
                logger.info("NettyRemotingServer bind success at port : {}", serverConfig.getListenPort());
            } else if (future.cause() != null) {
                throw new RuntimeException(String.format("NettyRemotingServer bind %s fail", serverConfig.getListenPort()), future.cause());
            } else {
                throw new RuntimeException(String.format("NettyRemotingServer bind %s fail", serverConfig.getListenPort()));
            }
        }
    }

    /**
     * init netty channel
     *
     * @param ch socket channel
     */
    private void initNettyChannel(SocketChannel ch) {
        ch.pipeline()
                .addLast(new NettyDecoder(RpcRequest.class))
                .addLast(new NettyEncoder())
                .addLast("server-idle-handle", new IdleStateHandler(0, 0, Constants.NETTY_SERVER_HEART_BEAT_TIME, TimeUnit.MILLISECONDS))
                .addLast("handler", new NettyServerHandler());
    }

    public void close() {
        if (isStarted.compareAndSet(true, false)) {
            try {
                if (bossGroup != null) {
                    this.bossGroup.shutdownGracefully();
                }
                if (workGroup != null) {
                    this.workGroup.shutdownGracefully();
                }

            } catch (Exception ex) {
                logger.error("netty server close exception", ex);
            }
            logger.info("netty server closed");
        }
    }

}
