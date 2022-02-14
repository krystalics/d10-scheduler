package com.github.krystalics.d10.scheduler.start.sharding;

import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.rpc.base.Rpc;

/**
 * @author linjiabao001
 * @date 2022/2/13
 * @description
 */
public interface IRebalanceService {
    @Rpc
    boolean shardBegin(JobInstance instance);

    @Rpc
    boolean shardEnd() throws InterruptedException;
}
