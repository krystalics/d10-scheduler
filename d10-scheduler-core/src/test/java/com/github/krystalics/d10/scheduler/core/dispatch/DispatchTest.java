package com.github.krystalics.d10.scheduler.core.dispatch;

import com.github.krystalics.d10.scheduler.rpc.api.ITaskRunnerService;
import com.github.krystalics.d10.scheduler.rpc.client.IRpcClient;
import com.github.krystalics.d10.scheduler.rpc.client.RpcClient;
import com.github.krystalics.d10.scheduler.rpc.config.NettyServerConfig;
import com.github.krystalics.d10.scheduler.rpc.remote.NettyClient;
import com.github.krystalics.d10.scheduler.rpc.remote.NettyServer;
import com.github.krystalics.d10.scheduler.rpc.utils.Host;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description
 */
public class DispatchTest {
    private ITaskRunnerService taskRunnerService;

    private Host host;

    @BeforeEach
    public void before() throws Exception {
        IRpcClient rpcClient = new RpcClient();
        host = new Host("127.0.0.1", 12346);
        taskRunnerService = rpcClient.create(ITaskRunnerService.class, host);
    }

    @Test
    public void dispatch() {
        taskRunnerService.addInstance(null);
    }

    @AfterEach
    public void after() {
        NettyClient.getInstance().close();
    }
}
