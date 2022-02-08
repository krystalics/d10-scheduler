package com.github.krystalics.d10.scheduler.executor.rpc;

import com.github.krystalics.d10.scheduler.rpc.api.INodeService;
import com.github.krystalics.d10.scheduler.rpc.base.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author linjiabao001
 * @Date 2022/2/8
 * @Description
 */
@RpcService("INodeService")
public class NodeServiceImpl implements INodeService {
    private static Logger log = LoggerFactory.getLogger(NodeServiceImpl.class);

    @Override
    public boolean isAlive() {
        log.info("check node is alive!");
        return true;
    }
}
