package com.github.krystalics.d10.scheduler.executor;

import com.github.krystalics.d10.scheduler.common.utils.SpringUtils;
import com.github.krystalics.d10.scheduler.executor.common.Constants;
import com.github.krystalics.d10.scheduler.executor.register.ExecutorStartHelper;
import com.github.krystalics.d10.scheduler.executor.utils.ClearUtils;
import com.github.krystalics.d10.scheduler.executor.utils.OSUtils;
import com.github.krystalics.d10.scheduler.executor.worker.InstanceRunPool;
import com.github.krystalics.d10.scheduler.rpc.config.NettyServerConfig;
import com.github.krystalics.d10.scheduler.rpc.remote.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.github.krystalics.d10.scheduler"})
public class ExecutorApplication {

    public static void main(String[] args) throws Exception {
        //启动时需要清理节点上它之前运行可能残留的任务
        ClearUtils.killAllSubProcesses();

        //start netty server
        final NettyServerConfig serverConfig = new NettyServerConfig();
        serverConfig.setListenPort(Constants.EXECUTOR_NETTY_SERVER_PORT);
        NettyServer nettyServer = new NettyServer(serverConfig);
        nettyServer.start();

        SpringApplication.run(ExecutorApplication.class, args);

        /**
         * register hooks, which are called before the process exits
         */
        Runtime.getRuntime().addShutdownHook(new Thread(InstanceRunPool::shutdownNow, "shutdown-hook"));
    }

    @Component
    @Slf4j
    static class ExecutorStartUp implements CommandLineRunner {

        @Autowired
        private ExecutorStartHelper startHelper;

        @Override
        public void run(String... args) throws Exception {
            startHelper.initCuratorCaches();
            startHelper.initZkPaths();
        }
    }

}
