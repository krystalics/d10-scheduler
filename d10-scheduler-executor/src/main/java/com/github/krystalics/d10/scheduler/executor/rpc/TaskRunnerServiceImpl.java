package com.github.krystalics.d10.scheduler.executor.rpc;

import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.rpc.api.ITaskRunnerService;
import com.github.krystalics.d10.scheduler.rpc.base.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description
 */
@RpcService("ITaskRunnerService")
public class TaskRunnerServiceImpl implements ITaskRunnerService {
    private static final Logger log = LoggerFactory.getLogger(TaskRunnerServiceImpl.class);

    @Override
    public boolean addInstance(VersionInstance instance) {
        log.info("i get the instance {}", instance);
        //todo put into thread pool
        return false;
    }
}
