package com.github.krystalics.d10.scheduler.executor;

import com.github.krystalics.d10.scheduler.executor.common.Constants;
import com.github.krystalics.d10.scheduler.rpc.config.NettyServerConfig;
import com.github.krystalics.d10.scheduler.rpc.remote.NettyServer;


/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description
 */
public class ExecutorApplication {
    public static void main(String[] args) {
        final NettyServerConfig serverConfig = new NettyServerConfig();
        serverConfig.setListenPort(Constants.EXECUTOR_SERVER_PORT);
        NettyServer nettyServer = new NettyServer(serverConfig);
        nettyServer.start();
    }
}
