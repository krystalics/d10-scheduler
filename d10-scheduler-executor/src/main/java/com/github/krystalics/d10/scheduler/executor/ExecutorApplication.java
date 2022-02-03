package com.github.krystalics.d10.scheduler.executor;

import com.github.krystalics.d10.scheduler.executor.common.Constants;
import com.github.krystalics.d10.scheduler.rpc.config.NettyServerConfig;
import com.github.krystalics.d10.scheduler.rpc.remote.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.github.krystalics.d10.scheduler"})
public class ExecutorApplication {
    public static void main(String[] args) {
        final NettyServerConfig serverConfig = new NettyServerConfig();
        serverConfig.setListenPort(Constants.EXECUTOR_SERVER_PORT);
        NettyServer nettyServer = new NettyServer(serverConfig);
        nettyServer.start();

        SpringApplication.run(ExecutorApplication.class, args);
    }


}
